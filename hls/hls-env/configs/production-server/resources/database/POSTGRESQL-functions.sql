###
CREATE OR REPLACE FUNCTION public.upsertStoreOTA(IN p_store_id bigint, IN p_ota_id bigint, IN p_url character varying, IN p_username character varying, IN p_password character varying, IN p_note text, IN p_custom_store_id character varying) RETURNS void AS $$
    
DECLARE
  l_pass_inc integer;
  l_expired_date date;
BEGIN

SELECT password_update_deadline into l_pass_inc FROM public.ota WHERE ota_id = p_ota_id;

IF l_pass_inc IS NOT NULL THEN
  l_expired_date = current_date + interval '1' day * l_pass_inc;
END IF;

INSERT INTO 
    
public.store_ota

(
  store_id, 
  ota_id, 
  url, 
  username, 
  password, 
  expired_date, 
  note,
  custom_store_id
)

VALUES 

(
  p_store_id,
  p_ota_id,
  p_url, 
  p_username,
  p_password,
  l_expired_date,
  p_note,
  p_custom_store_id
)
 
ON CONFLICT (store_id, ota_id)
DO UPDATE SET

  url= p_url, 
  username= p_username, 
  password= p_password, 
  note= p_note,
  custom_store_id= p_custom_store_id;
  
END;
  
$$  LANGUAGE plpgsql
###