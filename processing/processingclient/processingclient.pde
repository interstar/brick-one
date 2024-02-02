import websockets.*;

WebsocketClient wsc;

PVector[] pages = new PVector[2];

void setup() {
  size(500, 500);
  pages[0] = new PVector(0,0);
  pages[1] = new PVector(0,0);
  wsc= new WebsocketClient(this, "ws://localhost:8888/");
  frameRate(10);
}


void draw() {
  background(0);
  wsc.sendMessage("Client message");
  for (int cint=0;cint<2;cint++){
    fill(255, cint*127, 100);
    ellipse(pages[cint].x, pages[cint].y, 30, 30);
    println("#### " + pages[cint].x + "," + pages[cint].y + ","+cint);
  }
}


void webSocketEvent(String msg) {
  println(msg);
  String[] lines = msg.split("\n");
  for (String l : lines) {
    String[] parts = l.split("_");
    println("** " + parts[0] + "," + parts[1] + "," + parts[2]);
    if (parts[1].equals(":page/center")) {
      String[] coords = parts[2].split(" ");
      int x = Integer.valueOf(coords[1].replace(",", ""));
      int y = Integer.valueOf(coords[3].replace("}", ""));
      int cint = Integer.valueOf(parts[0]);
      pages[cint-1] = new PVector(x, y);
    }
  }
}
