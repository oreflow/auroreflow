import { Http, Response } from '@angular/http';
import { Injectable } from '@angular/core';

import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

import { Lightbulb } from '../model/lightbulb';

@Injectable()
export class LightbulbService {
    private LIST_LIGHTBULBS_PATH = '/lightbulb/list';

    constructor(private http: Http) {}

    getListofLightbulbs(): Observable<Lightbulb[]> {
        let observable =  this.http.get(this.LIST_LIGHTBULBS_PATH)
            .map(this.extractArrayData);
            observable.subscribe((data)=>{
                console.log(data);
            });
        return observable;
    };

    private extractArrayData(res: Response) {
        let body = res.json();
        return body.data || [];
    }
    private extractData(res: Response) {
        let body = res.json();
        return body.data || {};
    }
}