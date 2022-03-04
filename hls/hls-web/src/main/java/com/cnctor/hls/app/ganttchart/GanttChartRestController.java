package com.cnctor.hls.app.ganttchart;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.GanttChart;
import com.cnctor.hls.domain.model.GanttChartData;
import com.cnctor.hls.domain.repository.ganttchart.GanttChartSearchCriteria;
import com.cnctor.hls.domain.service.ganttchart.GanttChartService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class GanttChartRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  GanttChartService ganttChartService;
  
  @Inject
  GanttChartHelper ganttChartHelper;
  
  @PostMapping("/ganttchart")
  public @ResponseBody HlsResponse getGanttChartData(HttpServletRequest request,
      @RequestBody(required = false) GanttChartSearchForm searchForm,
      @RequestParam(defaultValue = "due_date") String sortBy,
      @RequestParam(defaultValue = "1") Long sortByType) {
    log.info("[DEBUG API Gantt Chart] : {}", request.isUserInRole("ROLE_ADMIN"));

    if (searchForm == null || searchForm.getStartDate() == null) {
      return HlsResponse.BADREQUEST(null, "ERROR-GANTTCHART-DATE-INVALID");
    }
    
    if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)
        || request.isUserInRole(Constants.ROLE_USER)) {
      GanttChartSearchCriteria criteria = new GanttChartSearchCriteria();
      String status = searchForm.getStatus();
      criteria.setSortBy(sortBy);
      criteria.setSortByType(sortByType);
      criteria.setStatus(status == null ? "": status);
      criteria.setStartDate(searchForm.getStartDate());
      criteria.setAssigneeId(searchForm.getAssigneeId());

      List<GanttChart> data = ganttChartService.getDataForGanttChart(criteria);
      if (data == null || data.size() == 0) {
        return HlsResponse.SUCCESS(null);
      } else {
        List<GanttChartData> result = ganttChartHelper.addChilds(data);
        GanttChartResultResponse response = new GanttChartResultResponse(result, result.size());
        return HlsResponse.SUCCESS(response);
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
    
    
  }
  
}
