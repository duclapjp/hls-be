/* define the schemas. */


/* define the Sequence. */

CREATE SEQUENCE IF NOT EXISTS public."SEQ_ACCOUNT"
    START WITH 10010
    INCREMENT BY 1
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_USERTOKEN"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;

CREATE SEQUENCE IF NOT EXISTS public."SEQ_CHAIN"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_STORE"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_OTAPASSWORDHISTORY"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_TASK"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    

CREATE SEQUENCE IF NOT EXISTS public."SEQ_TASK_STORE"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;   

CREATE SEQUENCE IF NOT EXISTS public."SEQ_COMMENT"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    

CREATE SEQUENCE IF NOT EXISTS public."SEQ_TASK_ATTACH"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_TASK_LOG"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1; 
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_NOTIFICATION"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;   
        
CREATE SEQUENCE IF NOT EXISTS public."SEQ_HELP"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;    
        
CREATE SEQUENCE IF NOT EXISTS public."SEQ_OTA"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_AMOUNT_RANK"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_AMOUNT_GROUP"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
    
    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_PLAN"
    INCREMENT 1
    START 10020
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;

/*    
CREATE SEQUENCE IF NOT EXISTS public."SEQ_ITEM"
    INCREMENT 1
    START 10010
    MINVALUE 10000
    MAXVALUE 999999999999999
    CACHE 1;
*/
    
/* define the Table. */

CREATE TABLE IF NOT EXISTS public.account (
    account_id integer NOT NULL,
    role character varying(50),
    status character varying(50),
    chain_id integer,
    store_id integer,
    mail character varying(500),
    phone character varying(500),
    note text,
    password character varying(500),
    display_name character varying(500),
    noti_dest character varying(100),
    mail_setting character varying(500),
    slack_setting character varying(500),
    chatwork_setting character varying(500),
    line_setting character varying(500),
    viber_rakuten_setting character varying(500),
    
    is_fist_login boolean NOT NULL DEFAULT true,
    
    CONSTRAINT account_pkey PRIMARY KEY (account_id)
);

/*
CREATE TABLE IF NOT EXISTS public.category (
    category_id integer NOT NULL,
    
    CONSTRAINT category_pkey PRIMARY KEY (category_id)
);
*/

CREATE TABLE IF NOT EXISTS public.chain (
    chain_id integer NOT NULL,
    contract_status character varying(50),
    name character varying(500),
    director_id_1 integer,
    director_id_2 integer,
    director_id_3 integer,
    manager_mail character varying(250),
    note text,
    manager_name character varying(500),
    manager_phone character varying(250),
    
    
    CONSTRAINT chain_pkey PRIMARY KEY (chain_id)
);

CREATE TABLE IF NOT EXISTS public.comment (
    comment_id integer NOT NULL,
    comment_text text,
    status character varying(50),
    assignee_id integer,
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    est_time integer,
    est_point integer,
    task_id integer,
    creator_id integer,
    type character varying(50),
    created_date timestamp with time zone,
    notify_to character varying(500),
    
    CONSTRAINT comment_pkey PRIMARY KEY (comment_id)
);

CREATE TABLE IF NOT EXISTS public.comment_account (
    comment_account_id integer NOT NULL,
    comment_id integer NOT NULL,
    account_id integer NOT NULL,
    
    CONSTRAINT comment_account_pkey PRIMARY KEY (comment_account_id)
);

CREATE TABLE IF NOT EXISTS public.help (
    help_id integer NOT NULL,
    manual_url character varying(500),
    manual_name character varying(500),
    created_date timestamp with time zone,
    file_size integer,
    
    CONSTRAINT help_pkey PRIMARY KEY (help_id)
);

CREATE TABLE IF NOT EXISTS public.notification (
    notification_id integer NOT NULL,
    title character varying(500),
    account_id integer,
    created_date timestamp with time zone,
    creator_id integer,
    recipient_id integer,
    action_id integer,
    action_value character varying(500),
    
    CONSTRAINT notification_pkey PRIMARY KEY (notification_id)
);

CREATE TABLE IF NOT EXISTS public.ota_type (
    ota_type_id integer NOT NULL,
    name character varying(500), 
    
    CONSTRAINT ota_type_pkey PRIMARY KEY (ota_type_id)
);

CREATE TABLE IF NOT EXISTS public.ota (
    ota_id integer NOT NULL,
    name character varying(500) NOT NULL, 
    
    ota_type_id integer NOT NULL,
    login_url_fixed1 character varying(500),
    login_url_fixed2 character varying(500),
    
    store_id integer,
    login_id character varying(500),
    password character varying(500),
    
    is_display_store_id boolean DEFAULT false,
    password_update_deadline integer,
    
    note text,
    status character varying(50),
    
    CONSTRAINT ota_pkey PRIMARY KEY (ota_id)
);

CREATE TABLE IF NOT EXISTS public.ota_password_history
(
    ota_password_history_id integer NOT NULL,
    ota_id integer,
    account_id integer,
    password character varying(500),
    updated_time timestamp with time zone,
    store_id integer,
    CONSTRAINT ota_password_history_pkey PRIMARY KEY (ota_password_history_id)
);

/*

CREATE TABLE IF NOT EXISTS public.site_controller (
    site_controller_id integer NOT NULL,
    name character varying(500),
    
    CONSTRAINT site_controller_pkey PRIMARY KEY (site_controller_id)
);

*/


CREATE TABLE IF NOT EXISTS public.store (
    store_id integer NOT NULL,
    chain_id integer,
    director_id integer,
    manager_name character varying(500),
    manager_phone character varying(250),
    manager_mail character varying(250),
    name character varying(500),
    contract_status character varying(50),
    note text,
    
    CONSTRAINT store_pkey PRIMARY KEY (store_id)
);

CREATE TABLE IF NOT EXISTS public.store_ota
(
    store_id integer NOT NULL,
    ota_id integer NOT NULL,
    
    custom_store_id character varying(500),
    url character varying(500),
    username character varying(500),
    password character varying(500),
    expired_date date,
    note text,
    
    CONSTRAINT store_ota_pkey PRIMARY KEY (store_id, ota_id)
);


CREATE TABLE IF NOT EXISTS public.task (
    task_id integer NOT NULL,
    category_id integer,
    title character varying(500),
    note text,
    status character varying(50),
    priority character varying(50),
    assignee_id integer,
    start_date timestamp with time zone,
    due_date timestamp with time zone,
    est_time integer,
    est_point integer,
    store_id integer,
    plan_id integer,
    
    CONSTRAINT task_pkey PRIMARY KEY (task_id)
);

CREATE TABLE IF NOT EXISTS public.task_log (
    task_log_id integer NOT NULL,
    task_log_date timestamp with time zone,
    account_id integer,
    action character varying(500),
    execute_time integer,
    accumulation_time integer,
    task_id integer,
    confirm_time integer,
    type character varying(50),
    
    CONSTRAINT task_log_pkey PRIMARY KEY (task_log_id)
);

CREATE TABLE IF NOT EXISTS public.task_store (
    task_store_id integer NOT NULL,
    task_id integer,
    store_id integer,
    
    CONSTRAINT task_store_pkey PRIMARY KEY (task_store_id)
);

CREATE TABLE IF NOT EXISTS public.user_token (
    user_token_id integer NOT NULL,
    token character varying(500) COLLATE pg_catalog."default",
    created_date timestamp with time zone,
    expired_date timestamp with time zone,
    action character varying COLLATE pg_catalog."default",
    user_id integer,
    is_active boolean NOT NULL DEFAULT true,
    action_value character varying(500),
    
    CONSTRAINT user_token_pkey PRIMARY KEY (user_token_id)
);

/*

CREATE TABLE IF NOT EXISTS public.store_site_controller
(
    url character varying(500),
    store_code character varying(250),
    username character varying(500),
    password character varying(500),
    expired_date timestamp with time zone,
    note text,
    store_id integer NOT NULL,
    site_controller_id integer NOT NULL,
    
    CONSTRAINT store_site_controller_pkey PRIMARY KEY (store_id, site_controller_id),
    
    CONSTRAINT store_site_controller_site_controller_id_fkey FOREIGN KEY (site_controller_id)
        REFERENCES public.site_controller (site_controller_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT store_site_controller_store_id_fkey FOREIGN KEY (store_id)
        REFERENCES public.store (store_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
);

*/

CREATE TABLE IF NOT EXISTS public.task_attach
(
    task_attach_id integer NOT NULL,
    task_id integer,
    attach_name character varying,
    attach_url character varying,
    created_date timestamp with time zone,
    parent_attach_id integer,
    file_size integer,
    
    CONSTRAINT task_attach_pkey PRIMARY KEY (task_attach_id),
    
    CONSTRAINT task_attach_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.task (task_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);


CREATE TABLE IF NOT EXISTS public.user_task_summary
(
    account_id integer NOT NULL,
    task_id integer NOT NULL,
    created_date timestamp with time zone,
    summary_time integer,
    latest_action character varying(50),
    latest_action_time timestamp with time zone,
    
    CONSTRAINT account_task_summary_pkey PRIMARY KEY (task_id, account_id),
    
    CONSTRAINT account_task_summary_account_id_fkey FOREIGN KEY (account_id)
        REFERENCES public.account (account_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID,
    CONSTRAINT account_task_summary_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.task (task_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

CREATE TABLE IF NOT EXISTS public.amount_rank
(
    amount_rank_id integer NOT NULL,
    store_id integer NOT NULL,
    amount_rank_no integer,
    amount_rank_name character varying(500),
    enable boolean NOT NULL DEFAULT false,
    created_date timestamp with time zone,
    updated_time timestamp with time zone,
    account_id integer NOT NULL,
    
    CONSTRAINT amount_rank_pkey PRIMARY KEY (amount_rank_id)
);

CREATE TABLE IF NOT EXISTS public.amount_group
(
    amount_group_id integer NOT NULL,
    store_id integer NOT NULL,
    amount_group_no integer,
    amount_group_name character varying(500),
    total_people integer NOT NULL,
    enable boolean NOT NULL DEFAULT false,
    created_date timestamp with time zone,
    updated_time timestamp with time zone,
    account_id integer NOT NULL,
    
    CONSTRAINT amount_group_pkey PRIMARY KEY (amount_group_id)
);

CREATE TABLE IF NOT EXISTS public.amount_group_rank
(
    amount_group_id integer NOT NULL,
    amount_rank_id integer NOT NULL,
    amounts character varying(500) COLLATE pg_catalog."default",
    CONSTRAINT amount_group_rank_pkey PRIMARY KEY (amount_group_id, amount_rank_id),
    CONSTRAINT amount_group_rank_amount_rank_id_fkey FOREIGN KEY (amount_rank_id)
        REFERENCES public.amount_rank (amount_rank_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
);

CREATE TABLE IF NOT EXISTS public.plan (

    plan_id integer NOT NULL,
    name character varying(500),
    c_order bigint,
    status character varying(50),
    created_date timestamp with time zone,
    updated_date timestamp with time zone,
    store_id integer,
    created_account_id integer,
    default_plan boolean DEFAULT false,
    available_for character varying(50),
    can_select_plan boolean DEFAULT false,
    
    CONSTRAINT plan_pkey PRIMARY KEY (plan_id)
);


CREATE TABLE IF NOT EXISTS public.item
(
    item_id integer NOT NULL,
    item_code character varying(50),
    name character varying(500),
    type character varying(50),
    show_in_default_plan boolean DEFAULT false,
    
    CONSTRAINT item_pkey PRIMARY KEY (item_id)
);


CREATE TABLE IF NOT EXISTS public.plan_item
(
    plan_id integer NOT NULL,
    item_id integer NOT NULL,
    item_json_value text,
    tab character varying(50),
    item_order integer,
    
    CONSTRAINT plan_item_pkey PRIMARY KEY (plan_id, item_id),
    CONSTRAINT plan_item_item_id_fkey FOREIGN KEY (item_id)
        REFERENCES public.item (item_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT plan_item_plan_id_fkey FOREIGN KEY (plan_id)
        REFERENCES public.plan (plan_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
);


CREATE TABLE IF NOT EXISTS public.task_item
(
    task_id integer NOT NULL,
    item_id integer NOT NULL,
    item_json_value text,
    
    CONSTRAINT task_item_pkey PRIMARY KEY (task_id, item_id),
    
    CONSTRAINT task_item_item_id_fkey FOREIGN KEY (item_id)
        REFERENCES public.item (item_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID,
    CONSTRAINT task_item_task_id_fkey FOREIGN KEY (task_id)
        REFERENCES public.task (task_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
        NOT VALID
);

/* DEFINE Function, Procedure */



/* END DEFINE Function, Procedure */



/* define Alter table . */
ALTER TABLE public.user_token ADD COLUMN IF NOT EXISTS action_value character varying(500);

ALTER TABLE public.chain ADD COLUMN IF NOT EXISTS manager_name character varying(500);
ALTER TABLE public.chain ADD COLUMN IF NOT EXISTS manager_phone character varying(250);

ALTER TABLE public.store ADD COLUMN IF NOT EXISTS contract_status character varying(50);
ALTER TABLE public.store ADD COLUMN IF NOT EXISTS manager_phone character varying(250);
ALTER TABLE public.store ADD COLUMN IF NOT EXISTS manager_mail character varying(250);


ALTER TABLE public.store DROP COLUMN IF EXISTS site_id;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_url;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_id_hotel;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_username;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_password;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_expired_date;
ALTER TABLE public.store DROP COLUMN IF EXISTS site_note;

ALTER TABLE public.ota DROP COLUMN IF EXISTS base_url;

ALTER TABLE public.ota_password_history ADD COLUMN IF NOT EXISTS store_id integer;

ALTER TABLE public.store ADD COLUMN IF NOT EXISTS note text;

ALTER TABLE public.task ADD COLUMN IF NOT EXISTS actual_time integer;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS director_id integer;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS register_date timestamp with time zone;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS register_person_id integer;
ALTER TABLE public.task ALTER COLUMN due_date TYPE timestamp with time zone;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS plan_id integer;

/*
ALTER TABLE public.category ADD COLUMN IF NOT EXISTS name character varying(250);
*/

ALTER TABLE public.store ADD COLUMN IF NOT EXISTS note text;

ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS task_id integer;
ALTER TABLE public.comment DROP COLUMN IF EXISTS created_date;
ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS due_date timestamp with time zone;
ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS start_date timestamp with time zone;

ALTER TABLE public.task ALTER COLUMN due_date TYPE timestamp with time zone;

-- ALTER TABLE public.task DROP COLUMN IF EXISTS attach_name;
-- ALTER TABLE public.task DROP COLUMN IF EXISTS attach_url;

ALTER TABLE public.task_log ADD COLUMN IF NOT EXISTS accumulation_time integer;

ALTER TABLE public.task ADD COLUMN IF NOT EXISTS store_id integer;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS parent_task_id integer;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS visible boolean NOT NULL DEFAULT false;

ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS creator_id integer;
ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS type character varying(50);

ALTER TABLE public.task_log ADD COLUMN IF NOT EXISTS confirm_time integer;
ALTER TABLE public.task_log ADD COLUMN IF NOT EXISTS type character varying(50);
ALTER TABLE public.task_log ADD COLUMN IF NOT EXISTS stopped boolean DEFAULT false;


ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS created_date timestamp with time zone;
ALTER TABLE public.comment ADD COLUMN IF NOT EXISTS notify_to character varying(500);

ALTER TABLE public.task_attach ADD COLUMN IF NOT EXISTS parent_attach_id integer;
ALTER TABLE public.task_attach ADD COLUMN IF NOT EXISTS file_size integer;

ALTER TABLE public.notification ADD COLUMN IF NOT EXISTS creator_id integer;
ALTER TABLE public.notification ADD COLUMN IF NOT EXISTS recipient_id integer;
ALTER TABLE public.notification ADD COLUMN IF NOT EXISTS action_id integer;
ALTER TABLE public.notification ADD COLUMN IF NOT EXISTS action_value character varying(500);

ALTER TABLE public.help ADD COLUMN IF NOT EXISTS manual_name character varying(500);
ALTER TABLE public.help ADD COLUMN IF NOT EXISTS created_date timestamp with time zone;
ALTER TABLE public.help ADD COLUMN IF NOT EXISTS file_size integer;

ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS ota_type_id integer NOT NULL;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS login_url_fixed1 character varying(500);
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS login_url_fixed2 character varying(500);
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS store_id integer;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS login_id character varying(500) NOT NULL;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS password character varying(500) NOT NULL;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS password_update_deadline integer;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS note text;
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS status character varying(50);
ALTER TABLE public.ota ADD COLUMN IF NOT EXISTS is_display_store_id boolean DEFAULT false;

ALTER TABLE public.ota ALTER COLUMN login_id DROP NOT NULL;
ALTER TABLE public.ota ALTER COLUMN password DROP NOT NULL;

ALTER TABLE public.account ADD COLUMN IF NOT EXISTS is_fist_login boolean;

DROP TABLE IF EXISTS public.site_controller CASCADE;
DROP TABLE IF EXISTS public.store_site_controller CASCADE;

ALTER TABLE public.store_ota ADD COLUMN IF NOT EXISTS custom_store_id character varying(500);

ALTER TABLE public.plan ADD COLUMN IF NOT EXISTS created_account_id integer;
ALTER TABLE public.plan ADD COLUMN IF NOT EXISTS default_plan boolean DEFAULT false;
ALTER TABLE public.plan ADD COLUMN IF NOT EXISTS available_for character varying(50);
ALTER TABLE public.plan ADD COLUMN IF NOT EXISTS can_select_plan boolean DEFAULT false;

ALTER TABLE public.plan_item ADD COLUMN IF NOT EXISTS item_json_value text;
ALTER TABLE public.plan_item ADD COLUMN IF NOT EXISTS tab character varying(50);
ALTER TABLE public.plan_item ADD COLUMN IF NOT EXISTS item_order integer;

DROP TABLE IF EXISTS public.category;
ALTER TABLE public.task ADD COLUMN IF NOT EXISTS category_id integer;

ALTER TABLE public.item ADD COLUMN IF NOT EXISTS show_in_default_plan boolean;