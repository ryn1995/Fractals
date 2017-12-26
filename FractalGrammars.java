// FractalGrammars.java
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.QuadCurve2D;

public class FractalGrammars extends Frame
{  public static void main(String[] args)
   {  if (args.length == 0)
         System.out.println("Use filename as program argument.");
      else
         new FractalGrammars(args[0]);
   }
   FractalGrammars(String fileName)
   {  super("Click left or right mouse button to change the level");
      addWindowListener(new WindowAdapter()
         {public void windowClosing(
                 WindowEvent e){System.exit(0);}});
      setSize (800, 600);
      add("Center", new CvFractalGrammars(fileName));
      show();
  }
}

class CvFractalGrammars extends Canvas
{  String fileName, axiom, strF, strf, strX, strY;
   int maxX, maxY, level = 1;
   double xLast, yLast, dir, rotation, dirStart, fxStart, fyStart,
      lengthFract, reductFact, prevDir;
   boolean Plus = false, Minus = false, First = true;

   void error(String str)
   {  System.out.println(str);
      System.exit(1);
   }

   CvFractalGrammars(String fileName)
   {  Input inp = new Input(fileName);
      if (inp.fails())
         error("Cannot open input file.");
      axiom = inp.readString(); inp.skipRest();
      strF = inp.readString(); inp.skipRest();
      strf = inp.readString(); inp.skipRest();
      strX = inp.readString(); inp.skipRest();
      strY = inp.readString(); inp.skipRest();
      rotation = inp.readFloat(); inp.skipRest();
      dirStart = inp.readFloat(); inp.skipRest();
      fxStart = inp.readFloat(); inp.skipRest();
      fyStart = inp.readFloat(); inp.skipRest();
      lengthFract = inp.readFloat(); inp.skipRest();
      reductFact = inp.readFloat();
      if (inp.fails())
         error("Input file incorrect.");

      addMouseListener(new MouseAdapter()
      {  public void mousePressed(MouseEvent evt)
         {  if ((evt.getModifiers() & InputEvent.BUTTON3_MASK) != 0)
            {  level--;      // Right mouse button decreases level
               if (level < 1)
                  level = 1;
            }
            else
               level++;      // Left mouse button increases level
            repaint();
         }
      });

   }

   Graphics g;
   int iX(double x){return (int)Math.round(x);}
   int iY(double y){return (int)Math.round(maxY-y);}

   void drawTo(Graphics g, double x, double y)
   {  g.drawLine(iX(xLast), iY(yLast), iX(x) ,iY(y));
      xLast = x;
      yLast = y;
   }
   
   void drawToCurve(Graphics g, double x, double y)
   {  
	   g.drawLine(iX(xLast), iY(yLast), iX(x) ,iY(y));
      xLast = x;
      yLast = y;
   }

   void moveTo(Graphics g, double x, double y)
   {  xLast = x;
      yLast = y;
   }
   public void paint(Graphics g)
   {  Dimension d = getSize();
      maxX = d.width - 1;
      maxY = d.height - 1;
      xLast = fxStart * maxX;
      yLast = fyStart * maxY;
      dir = dirStart;   // Initial direction in degrees
      turtleGraphics(g, axiom, level, lengthFract * maxY);
   }

   public void turtleGraphics(Graphics g, String instruction,
      int depth, double len)
   {  double xMark=0, yMark=0, dirMark=0;
      for (int i=0;i<instruction.length();i++)
      {  char ch = instruction.charAt(i);
         switch(ch)
         {
         case 'F': // Step forward and draw
            // Start: (xLast, yLast), direction: dir, steplength: len
            if (depth == 0)
            {  double rad = Math.PI/180 * dir, // Degrees -> radians
                  dx = len * Math.cos(rad), dy = len * Math.sin(rad);
            	double savedDir = dir;
            	if(First){
            	drawTo(g, xLast + (dx*0.75), yLast + (dy*0.75));
               
            	}
            	else if(!First)
               {
	               if(prevDir < dir)
	               {
	            	   //If turning + direction
	            	 //Draw Curve
	                   dir -= rotation/2;
	                   rad = Math.PI/180 * dir; // Degrees -> radians
	                   dx = len * Math.cos(rad); dy = len * Math.sin(rad);
	                   drawTo(g, xLast + (dx*0.25), yLast + (dy*0.25));
	                   
	            	   Plus = false;
	               }
	               else if(prevDir > dir)
	               {
	            	   //If turning - direction
	            	 //Draw Curve
	            	   dir += rotation/2;
	                   rad = Math.PI/180 * dir; // Degrees -> radians
	                   dx = len * Math.cos(rad); dy = len * Math.sin(rad);
	                   drawTo(g, xLast + (dx*0.25), yLast + (dy*0.25));
	                   
	            	   Minus = false;
	               }
	               else
	               {
	            	   //If not changing directions
	            	   drawTo(g, xLast + (dx*0.25), yLast + (dy*0.25));
	               }
	               rad = Math.PI/180 * savedDir; // Degrees -> radians
	               dx = len * Math.cos(rad); dy = len * Math.sin(rad);
	               drawTo(g, xLast + (dx*0.5), yLast + (dy*0.5));
               }
               //Restore saved values
               dir = savedDir;
               prevDir = dir;
               First = false;
            }
            else
               turtleGraphics(g, strF, depth - 1, reductFact * len);
            /*
             * if (depth == 0)
            {  double rad = Math.PI/180 * dir, // Degrees -> radians
                  dx = len * Math.cos(rad), dy = len * Math.sin(rad);
               drawToCurve(g, xLast + dx, yLast + dy);
            }
            else
               turtleGraphics(g, strF, depth - 1, reductFact * len);
             */
            break;
         case 'f': // Step forward without drawing
            // Start: (xLast, yLast), direction: dir, steplength: len
            if (depth == 0)
            {  double rad = Math.PI/180 * dir, // Degrees -> radians
                  dx = len * Math.cos(rad), dy = len * Math.sin(rad);
               moveTo(g, xLast + dx, yLast + dy);
            }
            else
               turtleGraphics(g, strf, depth - 1, reductFact * len);
            break;
         case 'X':
            if (depth > 0)
               turtleGraphics(g, strX, depth - 1, reductFact * len);
            break;
         case 'Y':
            if (depth > 0)

					  

turtleGraphics(g, strY, depth - 1, reductFact * len);
            break;
         case '+': // Turn right
            dir -= rotation; Plus = true; break;
         case '-': // Turn left
            dir += rotation; Minus = true; break;
         case '[': // Save position and direction
            xMark = xLast; yMark = yLast; dirMark = dir; break;
         case ']': // Back to saved position and direction
            xLast = xMark; yLast = yMark; dir = dirMark; break;
         }
      }
   }
}