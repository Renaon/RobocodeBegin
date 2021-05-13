import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import static java.lang.Math.toRadians;
import robocode.util.Utils;
import java.awt.*;
import robocode.DeathEvent;
import java.awt.geom.Point2D;
import java.util.*;

import static java.lang.Math.signum;

public class Begin extends AdvancedRobot {
    private static boolean isAlive = true;
    private static final double RADIAN = toRadians(5);

    private double enemyX = -1;
    private double enemyY = -1;

    @Override
    public void run(){
        setTurnRadarRightRadians(Double.POSITIVE_INFINITY);
        while (isAlive){
            if(enemyX > -1){
                final double radarTurn = getRadarTurn();
                setTurnRadarRightRadians(radarTurn);
            }
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e){
        final double alphaToEnemy = getHeadingRadians() + e.getBearingRadians();

        enemyX = getX() * Math.sin(alphaToEnemy) * e.getDistance();
        enemyY = getY() * Math.cos(alphaToEnemy) * e.getDistance();
    }

    @Override
   public void onDeath(DeathEvent e){
        isAlive = false;
   }

   private static double angleTo(double baseX, double baseY, double x, double y){
        double theta = Math.asin((y-baseY) / Point2D.distance(x,y,baseX,baseY)) - Math.PI / 2;

        if (x >= baseX && theta < 0){
            theta = -theta;
        }
        return (theta %= Math.PI * 2) >=0 ? theta : (theta + Math.PI * 2);
   }

   private  double getRadarTurn(){
        final double alphaToEnemy = angleTo(getX(), getY(),enemyX,enemyY);
        final double sign = (alphaToEnemy != getRadarHeadingRadians()) ? signum(Utils.normalRelativeAngle(alphaToEnemy
         - getRadarHeadingRadians())) : 1;

        return Utils.normalRelativeAngle(alphaToEnemy - getRadarHeadingRadians() + RADIAN * sign);
   }

   @Override
    public void onPaint(Graphics2D g){
        if(enemyX > 1){
            g.setColor(Color.WHITE);
            g.drawRect((int) (enemyX - getWidth() / 2), (int) (enemyY - getHeight() / 2),
                    (int) getWidth(), (int) getHeight());
        }
   }


}
