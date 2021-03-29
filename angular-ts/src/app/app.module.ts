import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import {ApiModule, Configuration} from '../../openapi';
import {Settings} from './settings.model';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
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
