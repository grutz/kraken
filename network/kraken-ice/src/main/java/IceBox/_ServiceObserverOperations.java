// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.1

package IceBox;

// <auto-generated>
//
// Generated from file `IceBox.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


/**
 * An Observer interface implemented by admin clients
 * interested in the status of services
 * 
 * @see ServiceManager
 * 
 **/
public interface _ServiceObserverOperations
{
    void servicesStarted(String[] services, Ice.Current __current);

    void servicesStopped(String[] services, Ice.Current __current);
}