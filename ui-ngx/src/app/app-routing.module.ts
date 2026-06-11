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

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

/** Root routes: empty path redirects to `home` (authenticated app shell). */
const routes: Routes = [
  { path: '',
    redirectTo: 'home',
    pathMatch: 'full',
    data: {
      breadcrumb: {
        skip: true
      }
    }
  }
];

/** Registers application root router (`RouterModule.forRoot`). */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
/**
 * Angular NgModule bundling app routing (Angular routing).
 */
})
export class AppRoutingModule { }
