/* load the records. */

/* default admin */

INSERT INTO public.account(account_id, role, status, chain_id, store_id, mail, phone, note, password, display_name, noti_dest, mail_setting, slack_setting, chatwork_setting, line_setting, viber_rakuten_setting) 
VALUES (10000, 'ROLE_ADMIN', '利用中', null, null, 'hls-admin@gmail.com', '', '', '$2a$10$pXRk7t2O8zEP71GgqQmaWOWqjmYsoeG/p30rOaOTVu81xw6nSwlh.', 'HLS Admin', '', '', '', '', '', '') ON CONFLICT (account_id) DO NOTHING;

INSERT INTO public.account(account_id, role, status, chain_id, store_id, mail, phone, note, password, display_name, noti_dest, mail_setting, slack_setting, chatwork_setting, line_setting, viber_rakuten_setting) 
VALUES (10001, 'ROLE_SUBADMIN', '利用中', null, null, 'hls-subadmin@gmail.com', '', '', '$2a$10$pXRk7t2O8zEP71GgqQmaWOWqjmYsoeG/p30rOaOTVu81xw6nSwlh.', 'HLS Sub Admin', '', '', '', '', '', '') ON CONFLICT (account_id) DO NOTHING;

INSERT INTO public.account(account_id, role, status, chain_id, store_id, mail, phone, note, password, display_name, noti_dest, mail_setting, slack_setting, chatwork_setting, line_setting, viber_rakuten_setting) 
VALUES (10002, 'ROLE_CHAIN', '利用中', null, null, 'hls-chain@gmail.com', '', '', '$2a$10$pXRk7t2O8zEP71GgqQmaWOWqjmYsoeG/p30rOaOTVu81xw6nSwlh.', 'HLS Chain', '', '', '', '', '', '') ON CONFLICT (account_id) DO NOTHING;

INSERT INTO public.account(account_id, role, status, chain_id, store_id, mail, phone, note, password, display_name, noti_dest, mail_setting, slack_setting, chatwork_setting, line_setting, viber_rakuten_setting) 
VALUES (10003, 'ROLE_STORE', '利用中', null, null, 'hls-store@gmail.com', '', '', '$2a$10$pXRk7t2O8zEP71GgqQmaWOWqjmYsoeG/p30rOaOTVu81xw6nSwlh.', 'HLS Store', '', '', '', '', '', '') ON CONFLICT (account_id) DO NOTHING;

INSERT INTO public.account(account_id, role, status, chain_id, store_id, mail, phone, note, password, display_name, noti_dest, mail_setting, slack_setting, chatwork_setting, line_setting, viber_rakuten_setting) 
VALUES (10004, 'ROLE_USER', '利用中', null, null, 'hls-user@gmail.com', '', '', '$2a$10$pXRk7t2O8zEP71GgqQmaWOWqjmYsoeG/p30rOaOTVu81xw6nSwlh.', 'HLS User', '', '', '', '', '', '') ON CONFLICT (account_id) DO NOTHING;

/* default chain for demo */

INSERT INTO public.chain(chain_id, contract_status, name, director_id_1, director_id_2, director_id_3, manager_mail, note) 
VALUES (10000, '利用中', 'Four Seasons', 10000, null, null, 'manager_fourseasons@gmail.com', 'note') ON CONFLICT (chain_id) DO NOTHING;


INSERT INTO public.chain(chain_id, contract_status, name, director_id_1, director_id_2, director_id_3, manager_mail, note) 
VALUES (10001, '利用中', 'Holiday Inn', 10000, null, null, 'manager_fourseasons@gmail.com', 'note') ON CONFLICT (chain_id) DO NOTHING;


/* default store for demo */

INSERT INTO public.store(
  store_id, chain_id, director_id, manager_name, name)
  VALUES (10000, 10000, 10000, 'Store Manager 1' ,'Hotel 1') ON CONFLICT (store_id) DO NOTHING;
  
INSERT INTO public.store(
  store_id, chain_id, director_id, manager_name, name)
  VALUES (10001, 10000, 10000, 'Store Manager 2' , 'Hotel 2') ON CONFLICT (store_id) DO NOTHING;

INSERT INTO public.store(
  store_id, chain_id, director_id, manager_name, name)
  VALUES (10002, 10001, 10000, 'Store Manager 3' , 'Hotel 3') ON CONFLICT (store_id) DO NOTHING;


INSERT INTO public.store(
  store_id, chain_id, director_id, manager_name, name)
  VALUES (10003, 10001, 10000, 'Store Manager 4' , 'Hotel 4') ON CONFLICT (store_id) DO NOTHING;  

INSERT INTO public.ota_type(ota_type_id, name) VALUES (10000, 'サイトコントローラー') ON CONFLICT (ota_type_id) DO NOTHING;
INSERT INTO public.ota_type(ota_type_id, name) VALUES (10001, '自社予約システム') ON CONFLICT (ota_type_id) DO NOTHING;
INSERT INTO public.ota_type(ota_type_id, name) VALUES (10002, '国内OTA') ON CONFLICT (ota_type_id) DO NOTHING;
INSERT INTO public.ota_type(ota_type_id, name) VALUES (10003, '海外OTA') ON CONFLICT (ota_type_id) DO NOTHING;
INSERT INTO public.ota_type(ota_type_id, name) VALUES (10004, 'ホールセラー') ON CONFLICT (ota_type_id) DO NOTHING;
INSERT INTO public.ota_type(ota_type_id, name) VALUES (10005, 'その他') ON CONFLICT (ota_type_id) DO NOTHING;


/* commit */
commit;  