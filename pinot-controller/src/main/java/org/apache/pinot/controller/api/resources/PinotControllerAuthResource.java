/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.pinot.controller.api.resources;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import org.apache.commons.lang3.StringUtils;
import org.apache.pinot.controller.api.access.AccessControl;
import org.apache.pinot.controller.api.access.AccessControlFactory;
import org.apache.pinot.controller.api.access.AccessType;


@Api(tags = "Auth")
@Path("/")
public class PinotControllerAuthResource {

  @Inject
  private AccessControlFactory _accessControlFactory;

  @Context
  HttpHeaders _httpHeaders;

  @GET
  @Path("auth/verify")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Check whether authentication is enabled")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Allowed"), @ApiResponse(code = 403, message = "Forbidden"), @ApiResponse(code = 500, message = "Verification error")})
  public boolean verify(@ApiParam(value = "Table name without type") @QueryParam("tableName") String tableName,
      @ApiParam(value = "API access type") @QueryParam("accessType") AccessType accessType,
      @ApiParam(value = "Endpoint URL") @QueryParam("endpointUrl") String endpointUrl) {
    AccessControl accessControl = _accessControlFactory.create();

    if (StringUtils.isBlank(tableName)) {
      return accessControl.hasAccess(accessType, _httpHeaders, endpointUrl);
    }

    return accessControl.hasAccess(tableName, accessType, _httpHeaders, endpointUrl);
  }

  @GET
  @Path("auth/info")
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Retrieve auth workflow info")
  @ApiResponses(value = {@ApiResponse(code = 200, message = "Allowed")})
  public AccessControl.AuthWorkflowInfo info() {
    return _accessControlFactory.create().getAuthWorkflowInfo();
  }
}
