/*
 * Copyright 2011 Future Systems
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.krakenapps.webconsole.impl;

import org.apache.felix.ipojo.annotations.Component;
import org.apache.felix.ipojo.annotations.Provides;
import org.apache.felix.ipojo.annotations.Requires;
import org.apache.felix.ipojo.annotations.ServiceProperty;
import org.krakenapps.api.Script;
import org.krakenapps.api.ScriptFactory;
import org.krakenapps.webconsole.ProgramApi;
import org.krakenapps.webconsole.StaticResourceApi;
import org.krakenapps.webconsole.WebSocketServer;

@Component(name = "webconsole-script-factory")
@Provides
public class WebConsoleScriptFactory implements ScriptFactory {
	@SuppressWarnings("unused")
	@ServiceProperty(name = "alias", value = "webconsole")
	private String alias;
	
	@Requires
	private WebSocketServer server;

	@Requires
	private StaticResourceApi staticResourceApi;
	
	@Requires
	private ProgramApi programApi;

	@Override
	public Script createScript() {
		return new WebConsoleScript(server, staticResourceApi, programApi);
	}

}