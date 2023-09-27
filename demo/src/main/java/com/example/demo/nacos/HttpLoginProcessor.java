/*
 * Copyright 1999-2021 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demo.nacos;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.client.auth.impl.process.LoginProcessor;
import com.alibaba.nacos.client.naming.utils.UtilAndComs;
import com.alibaba.nacos.client.utils.ContextPathUtil;
import com.alibaba.nacos.client.utils.ParamUtil;
import com.alibaba.nacos.common.http.HttpRestResult;
import com.alibaba.nacos.common.http.client.NacosRestTemplate;
import com.alibaba.nacos.common.http.param.Header;
import com.alibaba.nacos.common.http.param.Query;
import com.alibaba.nacos.common.utils.InternetAddressUtil;
import com.alibaba.nacos.common.utils.JacksonUtils;
import com.alibaba.nacos.common.utils.StringUtils;
import com.alibaba.nacos.plugin.auth.api.LoginIdentityContext;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.api.common.Constants.HTTP_PREFIX;
import static com.alibaba.nacos.client.naming.utils.UtilAndComs.webContext;

/**
 * Login processor for Http.
 *
 * @author Nacos
 */
public class HttpLoginProcessor implements LoginProcessor {
    private static final Logger SECURITY_LOGGER = LoggerFactory.getLogger(com.alibaba.nacos.client.auth.impl.process.HttpLoginProcessor.class);
    private static final String LOGIN_URL = "/v1/auth/users/login";
    private final NacosRestTemplate nacosRestTemplate;

    public HttpLoginProcessor(NacosRestTemplate nacosRestTemplate) {
        this.nacosRestTemplate = nacosRestTemplate;
    }

    public LoginIdentityContext getResponse(Properties properties) {
        String contextPath = ContextPathUtil.normalizeContextPath(properties.getProperty("contextPath", UtilAndComs.webContext));
        String server = properties.getProperty("server", "");
        if (!server.startsWith("https://") && !server.startsWith("http://")) {
            if (!InternetAddressUtil.containsPort(server)) {
                server = server + ":" + ParamUtil.getDefaultServerPort();
            }

            server = "http://" + server;
        }

        server.replace("http","https");

        String url = server + contextPath + "/v1/auth/users/login";
        Map<String, String> params = new HashMap(2);
        Map<String, String> bodyMap = new HashMap(2);
        params.put("username", properties.getProperty("username", ""));
        bodyMap.put("password", properties.getProperty("password", ""));

        try {
            HttpRestResult<String> restResult = this.nacosRestTemplate.postForm(url, Header.EMPTY, Query.newInstance().initParams(params), bodyMap, String.class);
            if (!restResult.ok()) {
                SECURITY_LOGGER.error("login failed: {}", JacksonUtils.toJson(restResult));
                return null;
            } else {
                JsonNode obj = JacksonUtils.toObj((String)restResult.getData());
                LoginIdentityContext loginIdentityContext = new LoginIdentityContext();
                if (obj.has("accessToken")) {
                    loginIdentityContext.setParameter("accessToken", obj.get("accessToken").asText());
                    loginIdentityContext.setParameter("tokenTtl", obj.get("tokenTtl").asText());
                } else {
                    SECURITY_LOGGER.info("[NacosClientAuthServiceImpl] ACCESS_TOKEN is empty from response");
                }

                return loginIdentityContext;
            }
        } catch (Exception var10) {
            SECURITY_LOGGER.error("[NacosClientAuthServiceImpl] login http request failed url: {}, params: {}, bodyMap: {}, errorMsg: {}", new Object[]{url, params, bodyMap, var10.getMessage()});
            return null;
        }
    }
}
