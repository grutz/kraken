// **********************************************************************
//
// Copyright (c) 2003-2010 ZeroC, Inc. All rights reserved.
//
// This copy of Ice is licensed to you under the terms described in the
// ICE_LICENSE file included in this distribution.
//
// **********************************************************************

// Ice version 3.4.1

package Ice;

// <auto-generated>
//
// Generated from file `LocalException.ice'
//
// Warning: do not edit this file.
//
// </auto-generated>


/**
 * This exception is
 * raised when a string conversion to or from UTF-8 fails during 
 * marshaling or unmarshaling.
 * 
 **/
public class StringConversionException extends MarshalException
{
    public StringConversionException()
    {
        super();
    }

    public StringConversionException(String reason)
    {
        super(reason);
    }

    public String
    ice_name()
    {
        return "Ice::StringConversionException";
    }
}