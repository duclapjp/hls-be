package com.cnctor.hls.app.listeners;

import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.domain.model.Item;
import com.cnctor.hls.domain.model.Plan;
import com.cnctor.hls.domain.service.item.ItemService;
import com.cnctor.hls.domain.service.plan.PlanService;
import com.cnctor.hls.domain.service.planitem.PlanItemService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {
  
  @Inject
  ItemService itemService;
  
  @Inject
  PlanService planService;
  
  @Inject
  PlanItemService piService;
  
  
  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    log.info("[StartupApplicationListener running after server startup]");
    try {
      
      // read xml file --> upsert or delete from db
      Resource rs = loadInitFiles();
      log.info("[Debug StartupApplicationListener]");
      
      Xml configs = new Xml(rs.getInputStream() ,"configs");
      List<Xml> xmlItems = configs.children("item");

      List<Item> items = new ArrayList<Item>();
      List<Item> delItems = new ArrayList<Item>();
      
      for (Xml itm : xmlItems) {
        Item nItem = new Item();
        nItem.setItemId(Long.parseLong(itm.child("itemId").content()));
        nItem.setItemCode(itm.child("itemCode").content());
        nItem.setName(itm.child("name").content());
        nItem.setType(itm.child("type").content());
        
        if(itm.child("showInDefaultPlan") != null && StringUtils.isNotBlank(itm.child("showInDefaultPlan").content())) {
          nItem.setShowInDefaultPlan(Boolean.valueOf(itm.child("showInDefaultPlan").content()));
        }
        
        log.info("{}", nItem);
        if(itm.child("deleted") != null && BooleanUtils.toBoolean(itm.child("deleted").content())) {
          delItems.add(nItem);
        } else {
          items.add(nItem);
        }
      }
      
      if(items.size() > 0) {
        itemService.upsert(items);
      }
      
      // delete item 
      if(delItems.size() > 0) {
        itemService.delete(delItems);
      }
      
      // insert default plan
      for (DefaultPlan dp : defaultPlans) {
        upsertDefaultPlan(dp.getPlanId(), dp.getName(), dp.getItemIds(), dp.getAvailableFor(), dp.isCanSelectPlan());
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }
  
  public Resource loadInitFiles() {
    return new ClassPathResource("META-INF/spring/init_items.xml");
  }
  
  public void upsertDefaultPlan(long id, String name, long[] itemIds,  String availableFor, boolean canSelectPlan) {
    
    Plan plan = new Plan();
    plan.setPlanId(id);
    plan.setName(name);
    plan.setStatus(Constants.PLAN_STATUS_ENABLED);
    plan.setDefaultPlan(true);
    plan.setAvailableFor(availableFor);
    plan.setCanSelectPlan(canSelectPlan);
    
    plan = planService.upsert(plan);
    
    // create plan item
    long planId = plan.getPlanId();
    
    boolean insertTabName = StringUtils.equalsIgnoreCase(name, CREATE_NEW_PLAN_NAME) || StringUtils.equalsIgnoreCase(name, UPDATE_PLAN_DETAIL_NAME);
    
    if(itemIds != null && itemIds.length > 0) {
      int itemOrder = 1;
      for (long itemId : itemIds) {
        if(insertTabName) {
          piService.upsert(planId, itemId, null, getTabName(itemId), itemOrder);
        } else {
          piService.upsert(planId, itemId, null, getOTATabName(itemId), itemOrder);
        }
        
        itemOrder++;
      }
    }
  }
  
  private String getTabName(long itemId) {
    String tabName = StringUtils.EMPTY;
    
    
    if(ArrayUtils.contains(PLAN_INFO_TAB, itemId)) {
      tabName = "PLAN_INFO_TAB";
    } 
    
    else if(ArrayUtils.contains(EXPLAIN_TAB, itemId)) {
      tabName = "EXPLAIN_TAB";
    } 
    
    else if(ArrayUtils.contains(PRICE_TAB, itemId)) {
      tabName = "PRICE_TAB";
    } 
    
    else if(ArrayUtils.contains(JARAN_TAB, itemId)) {
      tabName = "JARAN_TAB";
    } 
    
    else if(ArrayUtils.contains(RAKUTEN_TAB, itemId)) {
      tabName = "RAKUTEN_TAB";
    } 
    
    else if(ArrayUtils.contains(RURUBU_TAB, itemId)) {
      tabName = "RURUBU_TAB";
    } 
    
    else if(ArrayUtils.contains(YOYAKU_PRO_TAB, itemId)) {
      tabName = "YOYAKU_PRO_TAB";
    } 
    
    else if(ArrayUtils.contains(IKKYU_TAB, itemId)) {
      tabName = "IKKYU_TAB";
    } 
    
    else if(ArrayUtils.contains(DAIREKUTOIN_TAB, itemId)) {
      tabName = "DAIREKUTOIN_TAB";
    }
    
    else if(ArrayUtils.contains(OTHER_TAB, itemId)) {
      tabName = "OTHER_TAB";
    }
    
    log.info("[DEBUG getTabName] tabName : {}", tabName);
    return tabName;
  }
  
  
  private String getOTATabName(long itemId) {
    String tabName = StringUtils.EMPTY;
    
    if(ArrayUtils.contains(JARAN_TAB, itemId)) {
      tabName = "JARAN_TAB";
    } 
    
    else if(ArrayUtils.contains(RAKUTEN_TAB, itemId)) {
      tabName = "RAKUTEN_TAB";
    } 
    
    else if(ArrayUtils.contains(RURUBU_TAB, itemId)) {
      tabName = "RURUBU_TAB";
    } 
    
    else if(ArrayUtils.contains(YOYAKU_PRO_TAB, itemId)) {
      tabName = "YOYAKU_PRO_TAB";
    } 
    
    else if(ArrayUtils.contains(IKKYU_TAB, itemId)) {
      tabName = "IKKYU_TAB";
    } 
    
    else if(ArrayUtils.contains(DAIREKUTOIN_TAB, itemId)) {
      tabName = "DAIREKUTOIN_TAB";
    }
    
    return tabName;
  }
  
  private final long[] PLAN_INFO_TAB = {10011,10012,10014,10015,10082,10016,10017,10018,10019,10020,10021,10022,10025,10027,10032,10034};
  private final long[] EXPLAIN_TAB = {10013,10023,10024,10029,10033};   //TODO add them その他追加情報
  private final long[] PRICE_TAB = {10031};
  
  private final long[] JARAN_TAB = {10035,10036,10037,   10063,10064,10065,10066,10067};    //TODO  thieu ホームページダイレクト Homepage direct
  private final long[] RAKUTEN_TAB = {10038,10039,10040,10041,10042,10059,10060,10061,10062};
  private final long[] RURUBU_TAB = {10043,10068};
  private final long[] YOYAKU_PRO_TAB = {10044,10045,10046,10047,10048};
  private final long[] IKKYU_TAB = {10049,10050,    10069,10070};
  private final long[] DAIREKUTOIN_TAB = {10051};
  private final long[] OTHER_TAB = {10030,10071,10071,10072};
  
  private final long CREATE_NEW_PLAN_ID = 10000;
  private final String CREATE_NEW_PLAN_NAME = "新規プラン作成";
  private final long[] CREATE_NEW_PLAN = {10010,10011,10012,10013,10014,10015,10082,10016,10017,10018,10019,10020,10021,10022,10023,10024,10025,10027,10029,
      10030, 10031,10032,10033, 10034,10035,10036,10037,10038,10039,10040,10041,10042,10043,10044,10045,10046,10047,10048,10049,10050,10051,10071,10072};
  
  private final long UPDATE_PLAN_DETAIL_ID = 10001;
  private final String UPDATE_PLAN_DETAIL_NAME = "プラン詳細修正";
  private final long[] UPDATE_PLAN_DETAIL = {10010,10079,10011,10012,10013,10014,10015,10082,10016,10017,10018,10019,10020,10021,10022,10023,10024,10025,10027,
      10029,10030,10032,10033,10034,10035,10036,10038,10039,10040,10041,10042,10043,10044,10045,10046,10047,10048,10049,10051,10071,10072};
  
  private final long UPDATE_PLAN_PRICE_ID = 10002;
  private final String UPDATE_PLAN_PRICE_NAME = "プラン料金修正";
  private final long[] UPDATE_PLAN_PRICE = {10010,10079,10011,10012,10029,10030,10031};
  
  
  private final long CREATE_NEW_ROOM_ID = 10003;
  private final String CREATE_NEW_ROOM_NAME = "新規部屋作成";
  private final long[] CREATE_NEW_ROOM = {10010,10030,10052,10053,10054,10055,10056,10057,10058,10059,10060,10061,
      10062,10063,10064,10065,10066,10067,10068,10069,10070,10071,10072};
  
  private final long UPDATE_ROOM_DETAIL_ID = 10004;
  private final String UPDATE_ROOM_DETAIL_NAME = "部屋詳細修正";
  private final long[] UPDATE_ROOM_DETAIL = {10010,10030,10052,10053,10054,10055,10056,10057,10058,10059,10060,10062,
      10063,10064,10065,10066,10067,10068,10069,10070,10071,10072};
  
  private final long UPDATE_IMAGE_GALLERY_ID = 10005;
  private final String UPDATE_IMAGE_GALLERY_NAME = "フォトギャラリー修正";
  private final long[] UPDATE_IMAGE_GALLERY = {10010,10030,10071,10072,10073};
  
  
  private final long UPDATE_HOTEL_INFO_ID = 10006;
  private final String UPDATE_HOTEL_INFO_NAME = "施設情報修正";
  private final long[] UPDATE_HOTEL_INFO = {10010,10030};
  
  
  private final long REGIST_NEW_OTA_ID = 10007;
  private final String REGIST_NEW_OTA_NAME = "新規OTA登録";
  private final long[] REGIST_NEW_OTA = {10010,10030};
  
  
  private final long ACTUAL_CACULATION_ID = 10008;
  private final String ACTUAL_CACULATION_NAME = "実績計算";
  private final long[] ACTUAL_CACULATION = {10030,10074};
  
  
  private final long CREATE_POWERPOINT_ID = 10009;
  private final String CREATE_POWERPOINT_NAME = "パワポ作成";
  private final long[] CREATE_POWERPOINT = {10030,10075,10076};
  
  
  private final long PRICE_SURVEY_ID = 10010;
  private final String PRICE_SURVEY_NAME = "価格調査";
  private final long[] PRICE_SURVEY = {10010,10030,10077,10078,10079,10080};
  
  
  private final long FAQ_ID = 10011;
  private final String FAQ_NAME = "よくある問い合わせ";
  private final long[] FAQ = {10010,10030,10071,10081};
  
  private final long OTHERS_ID = 10012;
  private final String OTHERS_NAME = "その他";
  private final long[] OTHERS = {10010,10030};
  
  private DefaultPlan[] defaultPlans = {
      new DefaultPlan(CREATE_NEW_PLAN_ID, CREATE_NEW_PLAN_NAME, CREATE_NEW_PLAN, "CHAIN_STORE", true),
      new DefaultPlan(UPDATE_PLAN_DETAIL_ID, UPDATE_PLAN_DETAIL_NAME, UPDATE_PLAN_DETAIL, "CHAIN_STORE", true),
      new DefaultPlan(UPDATE_PLAN_PRICE_ID, UPDATE_PLAN_PRICE_NAME, UPDATE_PLAN_PRICE, "CHAIN_STORE", false),
      new DefaultPlan(CREATE_NEW_ROOM_ID, CREATE_NEW_ROOM_NAME, CREATE_NEW_ROOM, "CHAIN_STORE", false),
      new DefaultPlan(UPDATE_ROOM_DETAIL_ID, UPDATE_ROOM_DETAIL_NAME, UPDATE_ROOM_DETAIL, "CHAIN_STORE", false),
      new DefaultPlan(UPDATE_IMAGE_GALLERY_ID, UPDATE_IMAGE_GALLERY_NAME, UPDATE_IMAGE_GALLERY, "CHAIN_STORE", false),
      new DefaultPlan(UPDATE_HOTEL_INFO_ID, UPDATE_HOTEL_INFO_NAME, UPDATE_HOTEL_INFO, "CHAIN_STORE", false),
      
      
      new DefaultPlan(REGIST_NEW_OTA_ID, REGIST_NEW_OTA_NAME, REGIST_NEW_OTA, "ADMIN", false),
      new DefaultPlan(ACTUAL_CACULATION_ID, ACTUAL_CACULATION_NAME, ACTUAL_CACULATION, "ADMIN", false),
      new DefaultPlan(CREATE_POWERPOINT_ID, CREATE_POWERPOINT_NAME, CREATE_POWERPOINT, "ADMIN", false),
      new DefaultPlan(PRICE_SURVEY_ID, PRICE_SURVEY_NAME, PRICE_SURVEY, "ADMIN", false),
      new DefaultPlan(FAQ_ID, FAQ_NAME, FAQ, "ADMIN", false),
      
      new DefaultPlan(OTHERS_ID, OTHERS_NAME, OTHERS, "CHAIN_STORE", false)
  };
  
  @Data
  @AllArgsConstructor
  class DefaultPlan {
    long planId;
    String name;
    long[] itemIds;
    String availableFor;
    boolean canSelectPlan;
  }
}
