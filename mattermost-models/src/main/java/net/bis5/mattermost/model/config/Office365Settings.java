package net.bis5.mattermost.model.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SSO settings for Office365.
 * 
 * @author Takayuki Maruyama
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Office365Settings extends SsoSettings {

    private String directoryId;

}