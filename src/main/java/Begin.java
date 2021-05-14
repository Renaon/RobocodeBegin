import robocode.AdvancedRobot;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;
import java.awt.*;
import robocode.DeathEvent;
import java.awt.geom.Point2D;
import java.util.*;

import static java.lang.Math.*;

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

                final double bodyTurn = getBodyTurn();
                setTurnRightRadians(bodyTurn);

                if(getDistanceRemaining() == 0){
                    final double distance = getDistance();
                    setAhead(distance);
                }
            }
            final double gunTurn  = getGunTurn();
            setTurnGunRightRadians(gunTurn);
            setFire(2);
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

   private double getDistance(){
        return 200-400 * random();
   }

   private double getBodyTurn(){
        //вычисление угла поворота
        final double alphaToMe = angleTo(enemyX, enemyY, getX(), getY());
        //пеленг противника
        final double lateralDirection = signum((getVelocity() != 0 ? getVelocity() : 1) * 
                sin(Utils.normalRelativeAngle(getHeadingRadians() - alphaToMe)));
        //направление движения
       final double desiredHeading = Utils.normalAbsoluteAngle(alphaToMe + Math.PI / 2 * lateralDirection);
       //нормализуем направление по скорости
       final double normalHeading = getVelocity() >= 0 ? getHeadingRadians() :
               Utils.normalAbsoluteAngle(getHeadingRadians() * Math.PI);
       return Utils.normalRelativeAngle(desiredHeading - normalHeading);
   }

   private double getGunTurn(){
        return Utils.normalRelativeAngle(angleTo(getX(), getY(), enemyX, enemyY) - getGunHeadingRadians());
   }
}
