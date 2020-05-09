import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject, WebSocketSubjectConfig } from 'rxjs/webSocket';
import { HOST } from 'src/environments/host';

const URL = `ws://${HOST.hostname}:${HOST.port}/bot`

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  constructor() { }

  /**
   * Open web socket connection
   */
  openWsConn(): WebSocketSubject<string> {
    console.log(`Connecting to ${URL}`);
    let wssc: WebSocketSubjectConfig<string> = { url: URL, closeObserver: { next: () => { console.log("Connect Closed"); } } };
    let wss: WebSocketSubject<string> = webSocket(wssc);
    return wss;
  }
}
