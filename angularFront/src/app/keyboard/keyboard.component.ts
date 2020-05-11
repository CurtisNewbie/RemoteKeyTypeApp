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
    SPACE: "space",
    Q: "q",
    W: "w",
    E: "e",
    R: "r",
    T: "t",
    Y: "y",
    U: "u",
    I: "i",
    O: "o",
    P: "p",
    A: "a",
    S: "s",
    D: "d",
    F: "f",
    G: "g",
    H: "h",
    J: "j",
    K: "k",
    L: "l",
    Z: "z",
    X: "x",
    C: "c",
    V: "v",
    B: "b",
    N: "n",
    M: "m",
    BACKSPACE: "backspace",
    COMMA: "comma",
    PERIOD: "period",
  }

  wss: WebSocketSubject<string> = null;
  authKey: string;
  authKeySent: boolean;
  serverIp: string = "";

  constructor(private websocket: WebsocketService) { }

  ngOnInit() {
  }

  /**
   * Establish connectin with the server using websocket
   */
  connectServer(): void {
    if (this.serverIp) {
      this.wss = this.websocket.openWsConn(this.serverIp, () => {
        alert("Cannot connect to server. Please make sure to enter the correct IP address.");
        this.reset();
      });
      this.wss.subscribe({
        error: (err) => console.log,
        complete: () => { alert("Connection Closed."); this.reset(); }
      });
      this.serverIp = "";
    }
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

  private reset(): void {
    this.authKey = null;
    this.authKeySent = false;
    this.wss = null;
  }
}
