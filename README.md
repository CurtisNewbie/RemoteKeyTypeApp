# RemoteKeyTypeApp
A simple webapp (Quarkus+Angular+BootStrap4) that hopefully allows users to remotely type keys (arrow keys and space) on a PC using their mobile devices.

**Prerequisite**
- Java 11

## Motivation
I want an app that allows me to simulate basic key typing on my PC/Laptop using my mobile devices. One use case will be, typing the arraw keys for seeking when I am watching movies. This way, I don't always need to use a wireless keyboard or seat in front of my computer. :D 

## Implementation Details

**Note: This webapp does not allow the user to type whichever keys they want, it checks the message from frontend, and generate the associated KeyEvents. If the associated keyEvent doesn't exist, it simply ignores it. This is to hopefully improves security. However, it doesn't prevent spoofing attack because of the lack of encryption for client-server communication. There is a chance where attacker may steal the key that you sent, and send the same key to the server before your message arrives, though it's quite unlikely, you should be aware of it. You should never use this app in a public network.**

**More details:**
- The communication between the backend and frontend is achieved through **WebSocket**. 
- The Backend only accepts instructions (which key to type) when the user is authenticated, and **there can only be one authenticated user**.
- The KeyEvents are received by the window or dialog that is focused. You will need to manully focus on the one that your want to control.
- The **authentication process** is as follows: 
    1. Server generates a random string as a key for authentication. This key is only displayed in the terminal, and it's only valid for a limited time (depends on configuration). 
    2. User is required to connect to the server and send a correct key for authentication. If the key is incorrect, the connection will be closed by the server. If the key is used, i.e., a user is authenticated with the key, this key will no longer be valid.
    3. Once a user is successfully authenticated, the server will close all the other connections. As long as this authenticated user remains connected, the server will reject all connections directly.
    4. When the authenticated user is disconnected or the key is expired, the server will generate a new key and display it in the terminal.

## Configuration

A few configurations are available in `./quarkus-back/src/main/resources/application.properties`:

    auth.key.length= #the length of the key that is generated for authentication
    auth.key.valid.sec= #how long the key remains valid (in second)

## DEMO

<img src="https://user-images.githubusercontent.com/45169791/81493368-6c8a1e80-92d2-11ea-8141-6b76ae5b1293.gif" />
