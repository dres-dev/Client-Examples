import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {ApiModule, Configuration} from '../../openapi';
import {Settings} from './settings.model';
import {HttpClientModule} from '@angular/common/http';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    // import HttpClientModule after BrowserModule.
    HttpClientModule,
    ApiModule.forRoot( () => {
      return new Configuration({
        basePath: Settings.basePath
        // , withCredentials: true
      });
    })
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
