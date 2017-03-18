import 'core-js';
import 'zone.js/dist/zone';
import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { BrowserModule }  from '@angular/platform-browser';
import { AppModule } from './app.module';

platformBrowserDynamic().bootstrapModule(AppModule, [BrowserModule]);