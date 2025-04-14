alter session set container = XEPDB1;

-- Special characters such as the ampersand will result in prompt without this directive.
SET DEFINE OFF;

-- Settings
insert into TEMPLATE_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('ADMIN_ROLE_NAME', 'template-admin', 'STRING', 'App-specific Admin Role Name', 'AUTH', 1);
insert into TEMPLATE_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_CDN', 'N', 'BOOLEAN', 'Smoothness weblib resources from CDN.  Defaults to No = serve files locally. CDN is for minified/combined files on shared Content Delivery Network (CDN) server - Nice for when multiple apps use same resources to have warm cache.', 'CDN', 1);
insert into TEMPLATE_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_SERVER', 'ace.jlab.org/cdn', 'STRING', 'Host name and port of content delivery network host for shared smoothness resources. Only used if SMOOTHNESS_LOCATION=CDN', 'CDN', 2);
insert into TEMPLATE_OWNER.SETTING (KEY, VALUE, TYPE, DESCRIPTION, TAG, WEIGHT) values ('SMOOTHNESS_VERSION', '4.11.0', 'STRING', 'Version of smoothness lib on CDN.  Only used if SMOOTHNESS_LOCATION=CDN', 'CDN', 3);