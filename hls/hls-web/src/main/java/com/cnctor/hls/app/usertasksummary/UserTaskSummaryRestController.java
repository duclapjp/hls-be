package com.cnctor.hls.app.usertasksummary;

import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.dozer.Mapper;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.cnctor.hls.app.task.TaskHelper;
import com.cnctor.hls.app.utils.Constants;
import com.cnctor.hls.app.utils.HlsResponse;
import com.cnctor.hls.domain.model.Task;
import com.cnctor.hls.domain.model.UserTaskSummary;
import com.cnctor.hls.domain.repository.usertasksummary.UserTaskSummarySearchCriteria;
import com.cnctor.hls.domain.service.task.TaskService;
import com.cnctor.hls.domain.service.usertasksummary.UserTaskSummaryService;

@RestController
@RequestMapping("/api")
public class UserTaskSummaryRestController {

  @Inject
  Mapper beanMapper;

  @Inject
  UserTaskSummaryService userTaskSummaryService;

  @Inject
  TaskService taskService;

  @Inject
  TaskHelper taskHelper;

  @PostMapping("/tasks/{id}/usertasksummaries")
  public @ResponseBody HlsResponse getUserTaskSummarys(HttpServletRequest request,
      @PathVariable("id") long taskId,
      @RequestBody UserTaskSummarySearchForm userTaskSummaryRequest) {
    if (taskHelper.hasViewTaskRole(request, taskId)) {
      Task task = taskService.findTask(taskId);
      if (task == null) {
        return HlsResponse.NOTFOUND();
      }

      if (request.isUserInRole(Constants.ROLE_ADMIN) || request.isUserInRole(Constants.ROLE_SUBADMIN)) {
        UserTaskSummarySearchCriteria searchCriteria =
            beanMapper.map(userTaskSummaryRequest, UserTaskSummarySearchCriteria.class);
        long total = userTaskSummaryService.countUserTaskSummary(taskId, searchCriteria);

        if (total == 0) {
          return HlsResponse.SUCCESS(null);
        } else {
          List<UserTaskSummary> UserTaskSummarys =
              userTaskSummaryService.searchUserTaskSummary(taskId, searchCriteria);
          UserTaskSummaryResultResponse response =
              new UserTaskSummaryResultResponse(UserTaskSummarys, total);
          return HlsResponse.SUCCESS(response);
        }
      } else {
        return HlsResponse.FORBIDDEN();
      }
    } else {
      return HlsResponse.FORBIDDEN();
    }
  }
}
