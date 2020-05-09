import { Component, OnInit } from '@angular/core';
import { WebsocketService } from '../websocket.service';
import { WebSocketSubject } from 'rxjs/webSocket';

@Component({
  selector: 'app-keyboard',
  templateUrl: './keyboard.component.html',
  styleUrls: ['./keyboard.component.css']
})
export class KeyboardComponent implements OnInit {
  /**
 * A list of key events that are listened by the backend server
 */
  readonly KEY_EVENT = {
    ARROW_UP: "arrow_up",
    ARROW_DOWN: "arrow_down",
    ARROW_LEFT: "arrow_left",
    ARROW_RIGHT: "arrow_right",
  }

  wss: WebSocketSubject<string> = null;
  authKey: string;
  authKeySent: boolean;

  constructor(private websocket: WebsocketService) { }

  ngOnInit() {
  }

  /**
   * Establish connectin with the server using websocket
   */
  connectServer(): void {
    this.wss = this.websocket.openWsConn();
    this.wss.subscribe({
      error: (err) => console.log,
      complete: () => { alert("Connection Closed."); this.authKey = null; this.authKeySent = false; this.wss = null; }
    });
  }

  /**
   * Send Instruction (e.g., typing keys) only when the authenticatin key is sent
   * 
   * @param instruction string that represents a predefined actin (use constants: e.g., ARROW_LEFT)
   */
  sendInstruction(instruction: string) {
    if (instruction && this.authKeySent && this.wss) {
      this.send(instruction.trim());
    }
  }

  /**
   * Send authentication key
   * @param key 
   */
  sendAuthKey(): void {
    if (this.authKey && this.wss) {
      this.send(this.authKey.trim());
      this.authKeySent = true;
    }
  }

  /**
   * Send message through web socket
   * @param msg 
   */
  private send(msg: string): void {
    this.wss.next(msg);
  }
}