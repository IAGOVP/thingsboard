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
import { createDefaultHttpOptions, defaultHttpOptionsFromConfig, RequestConfig } from './http-utils';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PageLink } from '@shared/models/page/page-link';
import { PageData } from '@shared/models/page/page-data';
import { Customer } from '@shared/models/customer.model';
import { SaveEntityParams } from '@shared/models/entity.models';

/**
 * Angular injectable service: customer (HTTP service layer).
 *
 * <p>HTTP wrappers in `@core/http` calling ThingsBoard REST API.
 */
@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  constructor(
    private http: HttpClient
  ) { }

  
  /**
   * get customers.
   *
   * @param pageLink pagination and sort parameters
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<PageData<Customer>> observable or value
   */


  public getCustomers(pageLink: PageLink, config?: RequestConfig): Observable<PageData<Customer>> {
    return this.http.get<PageData<Customer>>(`/api/customers${pageLink.toQuery()}`,
      defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customer.
   *
   * @param customerId customer UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Customer> observable or value
   */


  public getCustomer(customerId: string, config?: RequestConfig): Observable<Customer> {
    return this.http.get<Customer>(`/api/customer/${customerId}`, defaultHttpOptionsFromConfig(config));
  }

  
  /**
   * get customers by ids.
   *
   * @param customerIds customer ids (Array<string>)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Array<Customer>> observable or value
   */


  public getCustomersByIds(customerIds: Array<string>, config?: RequestConfig): Observable<Array<Customer>> {
    return this.http.get<Array<Customer>>(`/api/customers?customerIds=${customerIds.join(',')}`, defaultHttpOptionsFromConfig(config));
  }

  /** Calls ThingsBoard REST `/api/customer, ...`. */

  public saveCustomer(customer: Customer, config?: RequestConfig): Observable<Customer>;
  public saveCustomer(customer: Customer, saveParams: SaveEntityParams, config?: RequestConfig): Observable<Customer>;
  /**
   * POST/PUT entity — save customer.
   *
   * @param customer customer (Customer)
   * @param saveParamsOrConfig save params or config (SaveEntityParams | RequestConfig)
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   * @returns Observable<Customer> observable or value
   */
  public saveCustomer(customer: Customer, saveParamsOrConfig?: SaveEntityParams | RequestConfig, config?: RequestConfig): Observable<Customer> {
    return this.http.post<Customer>('/api/customer', customer, createDefaultHttpOptions(saveParamsOrConfig, config));
  }

  /**
   * DELETE — delete customer.
   *
   * @param customerId customer UUID
   * @param config optional HTTP request config (ignoreLoading, ignoreErrors, etc.)
   */

  public deleteCustomer(customerId: string, config?: RequestConfig) {
    return this.http.delete(`/api/customer/${customerId}`, defaultHttpOptionsFromConfig(config));
  }

}
