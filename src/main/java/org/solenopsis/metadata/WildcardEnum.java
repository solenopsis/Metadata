/*
 * Copyright (C) 2017 Scot P. Floess
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.solenopsis.metadata;

import org.solenopsis.keraiai.wsdl.metadata.DescribeMetadataObject;

/**
 * The metadata types that support wild cards - per
 * https://developer.salesforce.com/docs/atlas.en-us.api_meta.meta/api_meta/meta_types_list.htm
 *
 * @author Scot P. Floess
 */
public enum WildcardEnum {
    ACTION_LINK_GROUP_TEMPLATE("ActionLinkGroupTemplate"),
    APEX_CLASS("ApexClass"),
    APEX_COMPONENT("ApexComponent"),
    APEX_PAGE("ApexPage"),
    APEX_TRIGGER("ApexTrigger"),
    APP_MENU("AppMenu"),
    APPROVAL_PROCESS("ApprovalProcess"),
    ARTICLE_TYPE("ArticleType"),
    ASSIGNMENT_RULES("AssignmentRules"),
    AUTH_PROVIDER("AuthProvider"),
    AURA_DEFINITION_BUNDLE("AuraDefinitionBundle"),
    AUTO_RESPONSE_RULES("AutoResponseRules"),
    BASE_SHARING_RULE("BaseSharingRule"),
    BRANDING_SET("BrandingSet"),
    CALL_CENTER("CallCenter"),
    CERTIFICATE("Certificate"),
    CLEAN_DATA_SERVICE("CleanDataService"),
    COMMUNITY_ZONE("Community (Zone)"),
    COMMUNITY_TEMPLATE_DEFINITION("CommunityTemplateDefinition"),
    COMMUNITY_THEME_DEFINITION("CommunityThemeDefinition"),
    COMPACT_LAYOUT("CompactLayout"),
    CONNECTED_APP("ConnectedApp"),
    CONTENT_ASSET("ContentAsset"),
    CORS_WHITE_LIST_ORIGIN("CorsWhitelistOrigin"),
    CRITERIA_BASED_SHARING_RULE("CriteriaBasedSharingRule"),
    CUSTOM_APPLICATION("CustomApplication"),
    CUSTOM_APPLICATION_COMPONENT("CustomApplicationComponent"),
    CUSTOM_FEED_FILTER("CustomFeedFilter"),
    CUSTOM_METADATA_TYPES("Custom Metadata Types (CustomObject)"),
    CUSTOM_METADTA("CustomMetadata"),
    CUSTOM_LABELS("CustomLabels"),
    CUSTOM_OBJECT("CustomObject"),
    CUSTOM_OBJECT_TRANSLATION("CustomObjectTranslation"),
    CUSTOM_PAGE_WEBLINK("CustomPageWebLink"),
    CUSTOM_PERMISSION("CustomPermission"),
    CUSTOM_SITE("CustomSite"),
    CUSTOM_TAB("CustomTab"),
    DATA_CATEGORY_GROUP("DataCategoryGroup"),
    DELEGATE_GROUP("DelegateGroup"),
    DUPLICATE_RULE("DuplicateRule"),
    ECLAIR_GEO_DATA("EclairGeoData"),
    ENTITLEMENT_PROCESS("EntitlementProcess"),
    ENTITLEMENT_TEMPLATE("EntitlementTemplate"),
    EVENT_DELIVERY("EventDelivery"),
    EVENT_SUBSCRIPTION("EventSubscription"),
    EXTERNAL_SERVICE_REGISTRATION("ExternalServiceRegistration"),
    EXTERNAL_DATA_SOURCE("ExternalDataSource"),
    FIELD_SET("FieldSet"),
    FLEXI_PAGE("FlexiPage"),
    FLOW("Flow"),
    FLOW_DEFINITION("FlowDefinition"),
    GLOBAL_VALUE_SET("GlobalValueSet"),
    GLOBAL_VALUE_SET_TRANSLATION("GlobalValueSetTranslation"),
    GROUP("Group"),
    HOME_PAGE_COMPONENT("HomePageComponent"),
    HOME_PAGE_LAYOUT("HomePageLayout"),
    INSTALLED_PACKAGE("InstalledPackage"),
    KEYWORD_LIST("KeywordList"),
    LAYOUT("Layout"),
    LIVE_AGENT_CHAT("LiveChatAgentConfig"),
    LIVE_CHAT_BUTTON("LiveChatButton"),
    LIVE_CHAT_DEPLOYMENT("LiveChatDeployment"),
    LIVE_CHAT_SENSITIVE_DATA_RULE("LiveChatSensitiveDataRule"),
    MANAGED_TOPICS("ManagedTopics"),
    MATCHING_RULE("MatchingRule"),
    MILESTONE("MilestoneType"),
    MODERATION_RULE("ModerationRule"),
    NAMED_CREDENTIAL("NamedCredential"),
    NETWORK("Network"),
    OWNER_SHARING_RULE("OwnerSharingRule"),
    PATH_ASSISTANT("PathAssistant"),
    PERMISSION_SET("PermissionSet"),
    PLATFORM_CACHE_PARTITION("PlatformCachePartition"),
    PORTAL("Portal"),
    POST_TEMPLATE("PostTemplate"),
    PROFILE("Profile"),
    PROFILE_PASSWORD_POLICY("ProfilePasswordPolicy"),
    PROFILE_SESSION_SETTING("ProfileSessionSetting"),
    QUEUE("Queue"),
    QUICK_ACTION("QuickAction"),
    REPORT_TYPE("ReportType"),
    ROLE("Role"),
    SAML_SSO_CONFIG("SamlSsoConfig"),
    SCONTROL("Scontrol"),
    SHARING_RULES("SharingRules"),
    SHARING_SET("SharingSet"),
    SITE_DOT_COM("SiteDotCom"),
    SKILL("Skill"),
    STANDARD_VALUE_SET_TRANSLATION("StandardValueSetTranslation"),
    STATIC_RESOURCE("StaticResource"),
    SYNONYM_DICTIONARY("SynonymDictionary"),
    TERRITORY("Territory"),
    TERRITORY_2("Territory2"),
    TERRITORY_2_MODEL("Territory2Model"),
    TERRITORY_2_RULE("Territory2Rule"),
    TERRITORY_2_TYPE("Territory2Type"),
    TRANSACTION_SECURITY_POLICY("TransactionSecurityPolicy"),
    TRANSLATIONS("Translations"),
    WAVE_APPLICATION("WaveApplication"),
    WAVE_DASHBOARD("WaveDashboard"),
    WAVE_DATAFLOW("WaveDataflow"),
    WAVE_DATASHEET("WaveDataset"),
    WAVE_LENS("WaveLens"),
    WAVE_TEMPLATE_BUNDLE("WaveTemplateBundle"),
    WAVE_X_MD("Wavexmd"),
    WORKFLOW("Workflow");

    final String metadataType;

    private WildcardEnum(final String metadataType) {
        this.metadataType = metadataType;
    }

    public String getMetadataType() {
        return metadataType;
    }

    boolean isMetadataType(final DescribeMetadataObject describeMetadataObject) {
        return metadataType.equals(describeMetadataObject.getXmlName());
    }

    static boolean isWildCardSupported(final DescribeMetadataObject describeMetadataObject) {
        for (final WildcardEnum wildcardEnum : WildcardEnum.values()) {
            if (wildcardEnum.isMetadataType(describeMetadataObject)) {
                return true;
            }
        }

        return false;
    }
}
