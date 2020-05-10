import { Injectable } from '@angular/core';
import { webSocket, WebSocketSubject, WebSocketSubjectConfig } from 'rxjs/webSocket';
import { HOST } from 'src/environments/host';


@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  constructor() { }

  /**
   * Open web socket connection
   */
  openWsConn(ip, onClose): WebSocketSubject<string> {
    console.log(`Connecting to '${ip}'`);
    let url = `ws://${ip}:${HOST.port}/bot`;
    let wssc: WebSocketSubjectConfig<string> = { url: url, closeObserver: { next: () => { onClose(); } } };
    let wss: WebSocketSubject<string> = webSocket(wssc);
    console.log(wss);
    return wss;
  }
}
