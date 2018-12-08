/*
 * Copyright 2018 The Code Department
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.tcdng.jacklyn.security.controllers;

import java.util.Date;
import java.util.List;

import com.tcdng.jacklyn.common.constants.RecordStatus;
import com.tcdng.jacklyn.security.constants.SecurityModuleAuditConstants;
import com.tcdng.jacklyn.security.constants.SecurityModuleErrorConstants;
import com.tcdng.jacklyn.security.constants.SecurityModuleSysParamConstants;
import com.tcdng.jacklyn.security.entities.User;
import com.tcdng.jacklyn.security.entities.UserRole;
import com.tcdng.jacklyn.security.entities.UserRoleQuery;
import com.tcdng.unify.core.ApplicationComponents;
import com.tcdng.unify.core.UnifyError;
import com.tcdng.unify.core.UnifyException;
import com.tcdng.unify.core.UserToken;
import com.tcdng.unify.core.annotation.Component;
import com.tcdng.unify.core.annotation.UplBinding;
import com.tcdng.unify.core.security.TwoFactorAutenticationService;
import com.tcdng.unify.web.annotation.Action;
import com.tcdng.unify.web.annotation.ResultMapping;
import com.tcdng.unify.web.annotation.ResultMappings;
import com.tcdng.unify.web.ui.control.Table;
import com.tcdng.unify.web.ui.panel.SwitchPanel;

/**
 * Controller for user login.
 * 
 * @author Lateef Ojulari
 * @since 1.0
 */
@Component("/security/loginpage")
@UplBinding("web/security/upl/login.upl")
@ResultMappings({
		@ResultMapping(name = "refreshlogin",
				response = { "!refreshpanelresponse panels:$l{loginSequencePanel}" }),
		@ResultMapping(name = "switchlogin",
				response = { "!hidepopupresponse",
						"!switchpanelresponse panels:$l{loginSequencePanel.loginBodyPanel}" }),
		@ResultMapping(name = "switchchangepassword", response = {
				"!switchpanelresponse panels:$l{loginSequencePanel.changePasswordBodyPanel}" }),
		@ResultMapping(name = "switchrolepanel",
				response = { "!showpopupresponse popup:$s{selectRolePanel}" }) })
public class UserLoginController extends AbstractApplicationForwarderController {

	private String userName;

	private String password;

	private String token;

	private String oldPassword;

	private String newPassword;

	private String confirmPassword;

	private String loginMessage;

	private List<UserRole> userRoleList;

	private Table selectRoleTable;

	private boolean is2FA;

	public UserLoginController() {
		super(false, false);
	}

	@Action
	public String login() throws UnifyException {
		try {
			User userData = getSecurityModule().login(userName, password);
			userName = null;
			password = null;

			if (!userData.isReserved() && is2FA) {
				TwoFactorAutenticationService twoFactorAuthService
						= (TwoFactorAutenticationService) this.getComponent(
								ApplicationComponents.APPLICATION_TWOFACTORAUTHENTICATIONSERVICE);
				if (!twoFactorAuthService.authenticate(userName, token)) {
					throw new UnifyException(SecurityModuleErrorConstants.INVALID_ONETIME_PASSWORD);
				}
			}

			logUserEvent(SecurityModuleAuditConstants.LOGIN);
			setDisplayMessage(null);

			if (userData.isChangeUserPassword() && !userData.isReserved()) {
				oldPassword = null;
				newPassword = null;
				confirmPassword = null;
				return "switchchangepassword";
			}
			return selectRole();
		} catch (UnifyException e) {
			logError(e);
			UnifyError err = e.getUnifyError();
			setDisplayMessage(getSessionMessage(err.getErrorCode(), err.getErrorParams()));
		}
		return "refreshlogin";
	}

	@Action
	public String changeUserPassword() throws UnifyException {
		try {
			setDisplayMessage(null);
			getSecurityModule().changeUserPassword(oldPassword, newPassword);
			logUserEvent(SecurityModuleAuditConstants.CHANGE_PASSWORD);
			return selectRole();
		} catch (UnifyException e) {
			UnifyError err = e.getUnifyError();
			setDisplayMessage(getSessionMessage(err.getErrorCode(), err.getErrorParams()));
			if (SecurityModuleErrorConstants.USER_ROLE_NOT_ACTIVE_AT_CURRENTTIME
					.equals(err.getErrorCode())) {
				return "switchlogin";
			}
		}
		return "switchchangepassword";
	}

	@Action
	public String cancelChangeUserPassword() throws UnifyException {
		userName = null;
		password = null;
		setDisplayMessage(null);
		getSecurityModule().logout(false);
		return "switchlogin";
	}

	@Action
	public String selectUserRole() throws UnifyException {
		UserRole userRoleData = userRoleList.get(selectRoleTable.getViewIndex());
		userRoleList = null;
		return forwardToApplication(userRoleData);
	}

	@Action
	public String cancelSelectUserRole() throws UnifyException {
		return cancelChangeUserPassword();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getLoginMessage() {
		return loginMessage;
	}

	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}

	public List<UserRole> getUserRoleList() {
		return userRoleList;
	}

	public void setUserRoleList(List<UserRole> userRoleList) {
		this.userRoleList = userRoleList;
	}

	@Override
	protected void onSetPage() throws UnifyException {
		selectRoleTable = getPageWidgetByShortName(Table.class,
				"selectRolePanel.roleTablePanel.contentTbl");
	}

	@Override
	protected void onOpenPage() throws UnifyException {
		userName = null;
		password = null;
		token = null;
		newPassword = null;
		oldPassword = null;
		confirmPassword = null;
		SwitchPanel switchPanel = (SwitchPanel) getPanelByShortName("loginSequencePanel");
		switchPanel.switchContent("loginBodyPanel");
		setDisplayMessage(loginMessage);

		// Show/hide token field based on system parameter
		is2FA = getSystemBusinessModule().getSysParameterValue(boolean.class,
				SecurityModuleSysParamConstants.ENABLE_TWOFACTOR_AUTHENTICATION);
		setVisible("loginPanel.tokenField", is2FA);
	}

	private String selectRole() throws UnifyException {
		setDisplayMessage(null);

		// Get user roles that are active based on current time
		UserToken userToken = getUserToken();
		UserRole userRoleData = null;

		UserRoleQuery query = new UserRoleQuery();
		query.userLoginId(userToken.getUserLoginId());
		query.roleStatus(RecordStatus.ACTIVE);
		query.roleActiveTime(new Date());
		List<UserRole> userRoleList = getSecurityModule().findUserRoles(query);

		if (userRoleList.isEmpty()) {
			if (!userToken.isReservedUser()) {
				throw new UnifyException(
						SecurityModuleErrorConstants.USER_ROLE_NOT_ACTIVE_AT_CURRENTTIME);
			}
		} else {
			if (userRoleList.size() > 1) {
				this.userRoleList = userRoleList;
				selectRoleTable.reset();
				return "switchrolepanel";
			}
			userRoleData = userRoleList.get(0);
		}

		return forwardToApplication(userRoleData);
	}

	private void setDisplayMessage(String message) throws UnifyException {
		loginMessage = message;
	}
}