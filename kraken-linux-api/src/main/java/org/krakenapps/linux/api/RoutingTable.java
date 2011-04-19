package org.krakenapps.linux.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import org.krakenapps.linux.api.RoutingEntry.Flag;

public class RoutingTable {
	public static List<RoutingEntry> getRoutingEntries() {
		List<RoutingEntry> entries = new ArrayList<RoutingEntry>();
		java.lang.Process p = null;
		BufferedReader br = null;

		try {
			String line = null;

			p = Runtime.getRuntime().exec("route -ee");
			br = new BufferedReader(new InputStreamReader(p.getInputStream()));

			br.readLine(); // ignore header line
			br.readLine(); // ignore column name line

			while ((line = br.readLine()) != null) {
				String[] tokens = line.split("[\t| ]+");
				if (tokens.length != 11)
					continue;

				InetAddress destination = (!tokens[0].equals("default")) ? InetAddress.getByName(tokens[0]) : null;
				InetAddress gateway = (!tokens[1].equals("*")) ? InetAddress.getByName(tokens[1]) : null;
				InetAddress genmask = InetAddress.getByName(tokens[2]);
				Flag flags = new RoutingEntry.Flag(tokens[3]);
				int metric = Integer.parseInt(tokens[4]);
				int ref = Integer.parseInt(tokens[5]);
				int use = Integer.parseInt(tokens[6]);
				String iface = tokens[7];
				int mss = Integer.parseInt(tokens[8]);
				int window = Integer.parseInt(tokens[9]);
				int irtt = Integer.parseInt(tokens[10]);
				entries.add(new RoutingEntry(destination, gateway, genmask, flags, metric, ref, use, iface, mss,
						window, irtt));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (p != null)
				p.destroy();
			try {
				if (br != null)
					br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return entries;
	}

	public static String addRoutingEntries(RoutingEntry entry, boolean isHost) {
		String cmd = "route add";

		cmd += (isHost ? " -host " : " -net ") + entry.getDestination().getHostAddress();
		if (entry.getGenmask() != null)
			cmd += " netmask " + entry.getGenmask().getHostAddress();
		if (entry.getGateway() != null)
			cmd += " gw " + entry.getGateway().getHostAddress();
		if (entry.getMetric() != null)
			cmd += " metric " + entry.getMetric();
		if (entry.getFlags() != null) {
			if (entry.getFlags().isReject())
				cmd += " reject";
			if (entry.getFlags().isModified())
				cmd += " mod";
			if (entry.getFlags().isDynamically())
				cmd += " dyn";
			if (entry.getFlags().isReinstate())
				cmd += " reinstate";
		}
		if (entry.getIface() != null)
			cmd += " dev " + entry.getIface();
		if (entry.getMss() != null)
			cmd += " mss " + entry.getMss();
		if (entry.getWindow() != null)
			cmd += " window " + entry.getWindow();
		if (entry.getIrtt() != null)
			cmd += " irtt " + entry.getIrtt();

		return Util.run(cmd);
	}

	public static String deleteRoutingEntries(RoutingEntry entry, boolean isHost) {
		String cmd = "route del";

		cmd += (isHost ? " -host " : " -net ") + entry.getDestination().getHostAddress();
		if (entry.getGenmask() != null)
			cmd += " netmask " + entry.getGenmask().getHostAddress();
		if (entry.getGateway() != null)
			cmd += " gw " + entry.getGateway().getHostAddress();
		if (entry.getMetric() != null)
			cmd += " metric " + entry.getMetric();
		if (entry.getIface() != null)
			cmd += " dev " + entry.getIface();

		return Util.run(cmd);
	}

	public static RoutingEntry findRoute(InetAddress ip) {
		int target = toInt((Inet4Address) ip);

		for (RoutingEntry entry : RoutingTable.getRoutingEntries()) {
			int dst = toInt((Inet4Address) entry.getDestination());
			int mask = toInt((Inet4Address) entry.getGenmask());

			if (dst == (target & mask))
				return entry;
		}

		return null;
	}

	private static int toInt(Inet4Address addr) {
		byte[] b = addr.getAddress();
		int l = 0;

		for (int i = 0; i < 4; i++) {
			l <<= 8;
			l |= b[i] & 0xff;
		}

		return l;
	}
}