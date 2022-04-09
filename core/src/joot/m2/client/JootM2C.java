package joot.m2.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.github.jootnet.mir2.core.actor.Action;
import com.github.jootnet.mir2.core.actor.Direction;
import com.github.jootnet.mir2.core.actor.HumActionInfo;
import com.github.jootnet.mir2.core.actor.HumActionInfos;

import joot.m2.client.actor.Hum;
import joot.m2.client.map.MapActor;

public class JootM2C extends ApplicationAdapter {
	private Stage stage;
	
	/** 地图绘制 */
	private MapActor map = new MapActor();
	/** 地图事件 */
	private InputListener inputListenerInMap = new InputListenerInMap();
	private boolean mouseDown = false;
	private int mouseX;
	private int mouseY;
	private int mouseButton;
	/** 人物 */
	private Hum me;
	
	public JootM2C() {
		//Assets.init("D:\\Program Files (x86)\\盛大网络\\热血传奇");
		Assets.init("D:\\Program Files\\ShengquGames\\Legend of mir");
	}

	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());
		map.addListener(inputListenerInMap);
		
		me = new Hum("林星");
		Gdx.graphics.setTitle("将唐传奇" + "-" + me.getName());
		me.move(300, 300).setAction(HumActionInfos.StandSouth);
		map.enter("0").move(me.getX(), me.getY()).add(me);
		
		stage.addActor(map);
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void resize (int width, int height) {
		boolean canResize = true;
		if (width < 800) {
			width = 800;
			canResize = false;
		}
		if (height < 600) {
			height = 600;
			canResize = false;
		}
		if (!canResize)
			Gdx.graphics.setWindowedMode(width, height);

		stage.getViewport().update(width, height, true);
		map.setBounds(0, 0, width, height);
	}

	@Override
	public void render () {
		float delta = Gdx.graphics.getDeltaTime();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		me.act();
		map.move(me.getX(), me.getY());
		
		calcMeAction();
		map.setMeAction(me.getAction(), me.getActionTick());
		
		stage.act(delta);
		stage.draw();
	}

	@Override
	public void dispose () {
		stage.dispose();
	}
	
	/** 鼠标或手指(移动端)在地图上按下时 */
	private boolean mapTouchDown (InputEvent event, float x, float y, int pointer, int button) {
		mouseDown = true;
		mouseX = (int) x;
		mouseY = (int) y;
		mouseButton = button;
		return true;
	}

	/** 鼠标或手指(移动端)在地图上抬起时 */
	private void mapTouchUp (InputEvent event, float x, float y, int pointer, int button) {
		mouseDown = false;
	}
	
	/** 鼠标在地图上移动时 */
	private boolean mapMouseMoved (InputEvent event, float x, float y) {
		return false;
	}

	/** 鼠标或手指(移动端)在地图上按下并移动时 */
	public void mapTouchDragged(InputEvent event, float x, float y, int pointer) {
		mouseX = (int) x;
		mouseY = (int) y;
	}

	// 计算玩家动作
	private void calcMeAction() {
		HumActionInfo currentHumAction = me.getAction();
		int currentHumActionTick = me.getActionTick();
		
		// 下一步的动作
		HumActionInfo nextAction = calcMeNextAction(currentHumAction);		
		
		if (currentHumAction == nextAction) return;
		if (currentHumAction.act != Action.Stand && currentHumActionTick != 1) return; // 这一步还没走完，等一下下

		me.setAction(nextAction);
	}

	// 根据鼠标（手指）动作计算当前人物应该进行的动作
	private HumActionInfo calcMeNextAction(HumActionInfo currentHumAction) {
		if (!mouseDown) return HumActionInfos.stand(currentHumAction.dir);
		// 鼠标与屏幕中心x、y轴的距离
        int xx = mouseX - Gdx.graphics.getWidth() / 2;
        int yy = mouseY - Gdx.graphics.getHeight() / 2;
        
        if (Math.abs(xx) < 48 && Math.abs(yy) < 32) return HumActionInfos.stand(currentHumAction.dir);
        
        // 来自网络的虚拟摇杆算法，计算方向
        // 勾股定理求斜边
        double obl = Math.sqrt(Math.pow(xx, 2) + Math.pow(yy, 2));
        // 求弧度
        double rad = yy < 0 ? Math.acos(xx / obl) : (Math.PI * 2- Math.acos(xx / obl));
        // 弧度转角度
        double angle = 180 / Math.PI * rad;

        Direction dir = null;
        if ((angle >= 337.5 && angle <= 360) || angle < 22.5) {
        	dir = Direction.East;
        } else if (angle >= 22.5 && angle < 67.5) {
        	dir = Direction.SouthEast;
        } else if (angle >= 67.5 && angle < 112.5) {
        	dir = Direction.South;
        } else if (angle >= 112.5 && angle < 157.5) {
        	dir = Direction.SouthWest;
        } else if (angle >= 157.5 && angle < 202.5) {
        	dir = Direction.West;
        } else if (angle >= 202.5 && angle < 247.5) {
        	dir = Direction.NorthWest;
        } else if (angle >= 247.5 && angle < 292.5) {
        	dir = Direction.North;
        } else if (angle >= 292.5 && angle < 337.5) {
        	dir = Direction.NorthEast;
        }
        
        if (mouseButton == Buttons.LEFT) {
        	return HumActionInfos.walk(dir);
        } else if (mouseButton == Buttons.RIGHT) {
        	return HumActionInfos.run(dir);
        }
		return HumActionInfos.stand(dir);
	}
	
	private class InputListenerInMap extends InputListener {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			return JootM2C.this.mapTouchDown(event, x, y, pointer, button);
		}
		@Override
		public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
			JootM2C.this.mapTouchUp(event, x, y, pointer, button);
		}
		@Override
		public boolean mouseMoved(InputEvent event, float x, float y) {
			return JootM2C.this.mapMouseMoved(event, x, y);
		}
		@Override
		public void touchDragged(InputEvent event, float x, float y, int pointer) {
			JootM2C.this.mapTouchDragged(event, x, y, pointer);
		}
	}

}