package org.krakenapps.rpc.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.krakenapps.rpc.RpcBlockingTable;
import org.krakenapps.rpc.RpcConnection;
import org.krakenapps.rpc.RpcException;
import org.krakenapps.rpc.RpcMessage;
import org.krakenapps.rpc.RpcSession;
import org.krakenapps.rpc.RpcSessionEventCallback;
import org.krakenapps.rpc.RpcSessionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RpcSessionImpl implements RpcSession {
	private final Logger logger = LoggerFactory.getLogger(RpcSessionImpl.class.getName());
	private int id;
	private String serviceName;
	private RpcConnection connection;
	private Map<String, Object> props;
	private RpcSessionState state = RpcSessionState.Opened;
	private Set<Integer> blockingCalls;
	private Set<RpcSessionEventCallback> callbacks;

	public RpcSessionImpl(int id, String serviceName, RpcConnection connection) {
		this.id = id;
		this.serviceName = serviceName;
		this.connection = connection;
		this.props = new HashMap<String, Object>();
		this.blockingCalls = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());
		this.callbacks = Collections.newSetFromMap(new ConcurrentHashMap<RpcSessionEventCallback, Boolean>());
	}

	@Override
	public RpcSessionState getState() {
		return state;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public RpcConnection getConnection() {
		return connection;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public Object getProperty(String name) {
		return props.get(name);
	}

	@Override
	public void setProperty(String name, Object value) {
		props.put(name, value);
	}

	@Override
	public Object call(String method, Object[] params) throws RpcException, InterruptedException {
		return call(method, params, 0);
	}

	@Override
	public Object call(String method, Object[] params, long timeout) throws RpcException, InterruptedException {
		verify();

		// send rpc call message
		RpcConnection conn = getConnection();
		if (!method.equals("peering-request") && !method.equals("authenticate"))
			conn.waitPeering();

		RpcBlockingTable table = conn.getBlockingTable();

		int msgId = conn.nextMessageId();
		RpcMessage msg = RpcMessage.newCall(msgId, getId(), method, params);
		conn.send(msg);

		// set blocking call
		blockingCalls.add(msgId);

		// wait response infinitely
		RpcMessage message = null;
		if (timeout == 0)
			message = table.await(msgId);
		else {
			message = table.await(msgId, timeout);
		}

		blockingCalls.remove(msgId);
		if (message == null)
			throw new RpcException("rpc timeout: message " + msgId);

		// response received
		Object type = message.getHeader("type");

		if (type.equals("rpc-error")) {
			String cause = message.getString("cause");
			if (logger.isDebugEnabled())
				logger.debug("kraken-rpc: catching exception for id {}, method {}", msgId, method);

			throw new RpcException(cause);
		}

		if (type.equals("rpc-ret")) {
			if (logger.isDebugEnabled())
				logger.debug("kraken-rpc: response for id {}, method {}", msgId, method);

			return message.get("ret");
		}

		// unknown type of return message
		throw new RpcException("unknown rpc message type: " + type);
	}

	@Override
	public void post(String method, Object[] params) {
		verify();

		// send rpc call message
		RpcConnection conn = getConnection();
		conn.waitPeering();

		RpcMessage msg = RpcMessage.newPost(conn.nextMessageId(), getId(), method, params);
		conn.send(msg);
	}

	@Override
	public void close() {
		state = RpcSessionState.Closed;

		// cancel all blocking calls
		RpcConnection conn = getConnection();
		RpcBlockingTable table = conn.getBlockingTable();
		for (Integer msgId : blockingCalls)
			table.cancel(msgId);

		blockingCalls.clear();

		// invoke all session callbacks
		for (RpcSessionEventCallback callback : callbacks) {
			try {
				callback.sessionClosed(this);
			} catch (Exception e) {
				logger.warn("kraken-rpc: session callback should not throw exception", e);
			}
		}
	}

	private void verify() {
		if (state == RpcSessionState.Closed)
			throw new IllegalStateException("session closed: " + id);
	}

	@Override
	public void addListener(RpcSessionEventCallback callback) {
		callbacks.add(callback);
	}

	@Override
	public void removeListener(RpcSessionEventCallback callback) {
		callbacks.remove(callback);
	}

	@Override
	public String toString() {
		return String.format("id=%d, service=%s, peer=%s", id, serviceName, connection.getRemoteAddress());
	}
}