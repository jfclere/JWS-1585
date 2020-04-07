/*
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.coyote.ajp;

import org.junit.Test;

public class TestJWS1585
{
    @Test
    public void testJWS1585() throws Exception {
        SimpleAjpClient client = new SimpleAjpClient();
        client.setPort(8009);
        client.connect();
        client.setUri("/demo-1.0/test.jsp");
        client.setProtocol("HTTP/1.1");
        TesterAjpMessage req = client.createForwardMessage();
        req.appendInt(0); // zero header.
        req.addAttribute("javax.servlet.include.request_uri", "/");
        req.addAttribute("javax.servlet.include.path_info", "/toto");
        req.addAttribute("javax.servlet.include.servlet_path", "/");
        req.end();
        // req.appendInt(0xA00B); // host: localhost
        // req.appendString("localhost");
        // req.appendByte(0xFF); // end of request
        TesterAjpMessage res = client.sendMessage(req);
        if (res.readByte() != 0x04)
          throw (new Exception("No send headers"));
        System.out.println("return code: " + res.readInt());
        System.out.println(res.readString());
        int num = res.readInt();
        System.out.println("number of headers in the response: " + num);
        String contentlength = "";
        for (int i=0; i<num; i++) {
           int headercode = res.readInt();
           if (headercode == 0xA003) {
               contentlength = res.readString();
               System.out.println("Content-Length: " + contentlength);
           } else {
               System.out.println("HEADER: " + headercode + " " + res.readString());
           }
        }
        System.out.println("next is " + res.readByte());
        res = client.readMessage();
        if (res.readByte() != 0x03)
          throw (new Exception("Nothing sent!"));
        String result = res.readString();
        System.out.println(res.readString());
        if (result.indexOf('9') != -1) {
            client.disconnect();
            throw (new Exception("File: toto, processed as JSP"));
        } else {
            client.disconnect();
        }
    }
} 
