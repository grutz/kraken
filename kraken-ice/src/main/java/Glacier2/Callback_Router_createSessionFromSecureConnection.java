// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.1

package Glacier2;

// <auto-generated>
//
// Generated from file `Router.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


/**
 * Create a per-client session with the router. The user is
 * authenticated through the SSL certificates that have been
 * associated with the connection. If a {@link SessionManager} has been
 * installed, a proxy to a {@link Session} object is returned to the
 * client. Otherwise, null is returned and only an internal
 * session (i.e., not visible to the client) is created.
 * 
 * If a session proxy is returned, it must be configured to route
 * through the router that created it. This will happen automatically
 * if the router is configured as the client's default router at the
 * time the session proxy is created in the client process, otherwise
 * the client must configure the session proxy explicitly.
 * 
 **/

public abstract class Callback_Router_createSessionFromSecureConnection extends Ice.TwowayCallback
{
    public abstract void response(SessionPrx __ret);
    public abstract void exception(Ice.UserException __ex);

    public final void __completed(Ice.AsyncResult __result)
    {
        RouterPrx __proxy = (RouterPrx)__result.getProxy();
        SessionPrx __ret = null;
        try
        {
            __ret = __proxy.end_createSessionFromSecureConnection(__result);
        }
        catch(Ice.UserException __ex)
        {
            exception(__ex);
            return;
        }
        catch(Ice.LocalException __ex)
        {
            exception(__ex);
            return;
        }
        response(__ret);
    }
}