///
/// Copyright © 2016-2026 The Thingsboard Authors
///
/// Licensed under the Apache License, Version 2.0 (the "License");
/// you may not use this file except in compliance with the License.
/// You may obtain a copy of the License at
///
///     http://www.apache.org/licenses/LICENSE-2.0
///
/// Unless required by applicable law or agreed to in writing, software
/// distributed under the License is distributed on an "AS IS" BASIS,
/// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
/// See the License for the specific language governing permissions and
/// limitations under the License.
///

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { defaultHttpOptionsFromConfig, RequestConfig } from '@core/http/http-utils';
import { Observable } from 'rxjs';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import {
  Notification,
  NotificationDeliveryMethod,
  NotificationRequest,
  NotificationRequestInfo,
  NotificationRequestPreview,
  NotificationRule,
  NotificationSettings,
  NotificationTarget,
  NotificationTemplate,
  NotificationType,
  NotificationUserSettings,
  SlackChanelType,
  SlackConversation
} from '@shared/models/notification.models';
import { User } from '@shared/models/user.model';
import { isNotEmptyStr } from '@core/utils';
import { EntityType } from '@shared/models/entity-type.models';

/**
 * Angular injectable service: notification (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(
    private http: HttpClient
  ) {
  }

  
  /**
   * get notifications.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<Notification>> observable or value
   */


  public getNotifications(pageLink: PageLink, unreadOnly = false, config?: RequestConfig): Observable<PageData<Notification>> {
    return this.http.get<PageData<Notification>>(`/api/notifications${pageLink.toQuery()}&unreadOnly=${unreadOnly}`,
                                                  defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete notification.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteNotification(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/notification/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * mark notification as read.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public markNotificationAsRead(id: string, config?: RequestConfig): Observable<void> {
    return this.http.put<void>(`/api/notification/${id}/read`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * mark all notifications as read.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public markAllNotificationsAsRead(config?: RequestConfig): Observable<void> {
    return this.http.put<void>('/api/notifications/read', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — create notification request.
   *
   * @param notification notification (NotificationRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationRequest> observable or value
   */


  public createNotificationRequest(notification: NotificationRequest, config?: RequestConfig): Observable<NotificationRequest> {
    return this.http.post<NotificationRequest>('/api/notification/request', notification, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * send entities limit increase request.
   *
   * @param entityType entity type (EntityType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public sendEntitiesLimitIncreaseRequest(entityType: EntityType, config?: RequestConfig): Observable<void> {
    return this.http.post<void>(`/api/notification/entitiesLimitIncreaseRequest/${entityType}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification request by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationRequest> observable or value
   */


  public getNotificationRequestById(id: string, config?: RequestConfig): Observable<NotificationRequest> {
    return this.http.get<NotificationRequest>(`/api/notification/request/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get available delivery methods.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<NotificationDeliveryMethod>> observable or value
   */


  public getAvailableDeliveryMethods(config?: RequestConfig): Observable<Array<NotificationDeliveryMethod>> {
    return this.http.get<Array<NotificationDeliveryMethod>>(`/api/notification/deliveryMethods`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete notification request.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteNotificationRequest(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/notification/request/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification request preview.
   *
   * @param notification notification (NotificationRequest)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationRequestPreview> observable or value
   */


  public getNotificationRequestPreview(notification: NotificationRequest, config?: RequestConfig): Observable<NotificationRequestPreview> {
    return this.http.post<NotificationRequestPreview>('/api/notification/request/preview',
                                                       notification, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification requests.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<NotificationRequestInfo>> observable or value
   */


  public getNotificationRequests(pageLink: PageLink, config?: RequestConfig): Observable<PageData<NotificationRequestInfo>> {
    return this.http.get<PageData<NotificationRequestInfo>>(`/api/notification/requests${pageLink.toQuery()}`,
                                                        defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationSettings> observable or value
   */


  public getNotificationSettings(config?: RequestConfig): Observable<NotificationSettings> {
    return this.http.get<NotificationSettings>('/api/notification/settings', defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save notification settings.
   *
   * @param notificationSettings notification settings (NotificationSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationSettings> observable or value
   */


  public saveNotificationSettings(notificationSettings: NotificationSettings, config?: RequestConfig): Observable<NotificationSettings> {
    return this.http.post<NotificationSettings>('/api/notification/settings', notificationSettings, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * list slack conversations.
   *
   * @param type type (SlackChanelType)
   * @param token token (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<SlackConversation>> observable or value
   */


  public listSlackConversations(type: SlackChanelType, token?: string, config?: RequestConfig): Observable<Array<SlackConversation>> {
    let url = `/api/notification/slack/conversations?type=${type}`;
    if (isNotEmptyStr(token)) {
      url += `&token=${token}`;
    }
    return this.http.get<Array<SlackConversation>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save notification rule.
   *
   * @param notificationRule notification rule (NotificationRule)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationRule> observable or value
   */


  public saveNotificationRule(notificationRule: NotificationRule, config?: RequestConfig): Observable<NotificationRule> {
    return this.http.post<NotificationRule>('/api/notification/rule', notificationRule, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification rule by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationRule> observable or value
   */


  public getNotificationRuleById(id: string, config?: RequestConfig): Observable<NotificationRule> {
    return this.http.get<NotificationRule>(`/api/notification/rule/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete notification rule.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteNotificationRule(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/notification/rule/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification rules.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<NotificationRule>> observable or value
   */


  public getNotificationRules(pageLink: PageLink, config?: RequestConfig): Observable<PageData<NotificationRule>> {
    return this.http.get<PageData<NotificationRule>>(`/api/notification/rules${pageLink.toQuery()}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save notification target.
   *
   * @param notificationTarget notification target (NotificationTarget)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationTarget> observable or value
   */


  public saveNotificationTarget(notificationTarget: NotificationTarget, config?: RequestConfig): Observable<NotificationTarget> {
    return this.http.post<NotificationTarget>('/api/notification/target', notificationTarget, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification target by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationTarget> observable or value
   */


  public getNotificationTargetById(id: string, config?: RequestConfig): Observable<NotificationTarget> {
    return this.http.get<NotificationTarget>(`/api/notification/target/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete notification target.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteNotificationTarget(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/notification/target/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification targets by ids.
   *
   * @param ids ids (string[])
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<NotificationTarget>> observable or value
   */


  public getNotificationTargetsByIds(ids: string[], config?: RequestConfig): Observable<Array<NotificationTarget>> {
    return this.http.get<Array<NotificationTarget>>(`/api/notification/targets?ids=${ids.join(',')}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification targets.
   *
   * @param pageLink pagination and sort parameters
   * @param type type (NotificationType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<NotificationTarget>> observable or value
   */


  public getNotificationTargets(pageLink: PageLink, type?: NotificationType,
                                config?: RequestConfig): Observable<PageData<NotificationTarget>> {
    let url = `/api/notification/targets${pageLink.toQuery()}`;
    if (isNotEmptyStr(type)) {
      url += `&notificationType=${type}`;
    }
    return this.http.get<PageData<NotificationTarget>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get recipients for notification target config.
   *
   * @param notificationTarget notification target (NotificationTarget)
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<User>> observable or value
   */


  public getRecipientsForNotificationTargetConfig(notificationTarget: NotificationTarget, pageLink: PageLink,
                                                  config?: RequestConfig): Observable<PageData<User>> {
    return this.http.post<PageData<User>>(`/api/notification/target/recipients${pageLink.toQuery()}`, notificationTarget,
                                          defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save notification template.
   *
   * @param notificationTarget notification target (NotificationTemplate)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationTemplate> observable or value
   */


  public saveNotificationTemplate(notificationTarget: NotificationTemplate, config?: RequestConfig): Observable<NotificationTemplate> {
    return this.http.post<NotificationTemplate>('/api/notification/template', notificationTarget, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification template by id.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationTemplate> observable or value
   */


  public getNotificationTemplateById(id: string, config?: RequestConfig): Observable<NotificationTemplate> {
    return this.http.get<NotificationTemplate>(`/api/notification/template/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * DELETE — delete notification template.
   *
   * @param id id (string)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<void> observable or value
   */


  public deleteNotificationTemplate(id: string, config?: RequestConfig): Observable<void> {
    return this.http.delete<void>(`/api/notification/template/${id}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification templates.
   *
   * @param pageLink pagination and sort parameters
   * @param notificationTypes notification types (NotificationType)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<NotificationTemplate>> observable or value
   */


  public getNotificationTemplates(pageLink: PageLink, notificationTypes?: NotificationType,
                                  config?: RequestConfig): Observable<PageData<NotificationTemplate>> {
    let url = `/api/notification/templates${pageLink.toQuery()}`;
    if (isNotEmptyStr(notificationTypes)) {
      url += `&notificationTypes=${notificationTypes}`;
    }
    return this.http.get<PageData<NotificationTemplate>>(url, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get notification user settings.
   *
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationUserSettings> observable or value
   */


  public getNotificationUserSettings(config?: RequestConfig): Observable<NotificationUserSettings> {
    return this.http.get<NotificationUserSettings>(`/api/notification/settings/user`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * POST/PUT entity — save notification user settings.
   *
   * @param settings settings (NotificationUserSettings)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<NotificationUserSettings> observable or value
   */


  public saveNotificationUserSettings(settings: NotificationUserSettings, config?: RequestConfig): Observable<NotificationUserSettings> {
    return this.http.post<NotificationUserSettings>('/api/notification/settings/user', settings, defaultHttpOptionsFromConfig(config));
  }
}
