/*
 * @(#) net.bis5.mattermost.client4.api.ComplianceApi
 * Copyright (c) 2017 Maruyama Takayuki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * 	Unless required by applicable law or agreed to in writing, software
 * 	distributed under the License is distributed on an "AS IS" BASIS,
 * 	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 	See the License for the specific language governing permissions and
 * 	limitations under the License.
 */
package net.bis5.mattermost.client4.api;

import net.bis5.mattermost.client4.ApiResponse;
import net.bis5.mattermost.client4.Pager;
import net.bis5.mattermost.model.Compliance;
import net.bis5.mattermost.model.Compliances;

/**
 * TODO 型の説明
 * 
 * @author Maruyama Takayuki
 * @since 2017/09/09
 */
public interface ComplianceApi {

	ApiResponse<Compliance> createComplianceReport(Compliance report);

	default ApiResponse<Compliances> getComplianceReports() {
		return getComplianceReports(Pager.defaultPager());
	}

	ApiResponse<Compliances> getComplianceReports(Pager pager);

	ApiResponse<Compliance> getComplianceReport(String reportId);

	ApiResponse<Object> downloadComplianceReport(String reportId);

}
