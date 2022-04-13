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
	public static Hum me;
	
	public JootM2C() {
		Assets.init("D:\\Program Files (x86)\\盛大网络\\热血传奇");
		//Assets.init("D:\\Program Files\\ShengquGames\\Legend of mir");
	}

	@Override
	public void create () {
		stage = new Stage(new ScreenViewport());
		map.addListener(inputListenerInMap);
		
		me = new Hum("林星");
		Gdx.graphics.setTitle("将唐传奇" + "-" + me.getName());
		me.move(300, 300).setAction(HumActionInfos.StandSouth)
			.dress(0, 19) // 穿着雷霆
			.equip(0, 69) // 拿着开天
			.wing(0, 1); // 扑扇着白色翅膀
		map.enter("0").move(me.getX(), me.getY()).add(me);
		
		stage.addActor(map);
		stage.setKeyboardFocus(map);
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

		// 当前角色特殊处理
		calcMeAction();
		
		// 地图的视角和绘制偏移以当前角色为准
		map.move(me.getX(), me.getY());
		map.setMeAction(me.getAction(), me.getActionTick());
		
		Assets.update(40);
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
	private void mapTouchDragged(InputEvent event, float x, float y, int pointer) {
		mouseX = (int) x;
		mouseY = (int) y;
	}
	
	/** 键盘按下事件 */
	private boolean mapKeyUp(InputEvent event, int keycode) {

		return true;
	}

	// 计算玩家动作
	private void calcMeAction() {
		HumActionInfo humAction = me.getAction();
		int lastActionTick = me.getActionTick();
		me.act();
		int nextActionTick = me.getActionTick();
		
		HumActionInfo nextAction = calcMeNextAction(humAction);
		
		if (humAction == nextAction) return;
		if (humAction.act != Action.Stand && (nextActionTick != 1 || lastActionTick != humAction.frameCount)) return; // 这一步还没走完，等一下下

		me.setAction(nextAction);
	}

	// 根据鼠标（手指）动作计算当前人物应该进行的动作
	private HumActionInfo calcMeNextAction(HumActionInfo humAction) {
		if (!mouseDown) return HumActionInfos.stand(humAction.dir);
		// 鼠标与屏幕中心x、y轴的距离
		int[] mapCenter = map.humXY2MapXY(me.getX(), me.getY());
        int disX = mouseX - (mapCenter[0] + 24); // 脚踩地图块中心
        int disY = mouseY - (mapCenter[1] + 16);
        
        if (Math.abs(disX) < 24 && Math.abs(disY) < 16) return HumActionInfos.stand(humAction.dir);
        
        // 来自网络的虚拟摇杆算法，计算方向
        // 勾股定理求斜边
        double obl = Math.sqrt(Math.pow(disX, 2) + Math.pow(disY, 2));
        // 求弧度
        double rad = disY < 0 ? Math.acos(disX / obl) : (Math.PI * 2- Math.acos(disX / obl));
        // 弧度转角度
        double angle = 180 / Math.PI * rad;

        int moveStep = 0;
        if (mouseButton == Buttons.LEFT) {
        	moveStep = 1;
        } else if (mouseButton == Buttons.RIGHT) {
        	moveStep = 2;
        }
        
        Direction dir = null;
        boolean canWalk = false;
        if (angle >= 337.5 || angle < 22.5) {
        	dir = Direction.East;
            canWalk = map.isCanWalk(me.getX() + 1, me.getY()); // 目标位置（一个身位）是否可达
            if(moveStep == 2 && !map.isCanWalk(me.getX() + 2, me.getY())) // 若想要跑动，且目标不可达，则尝试改为走到最近的一个身位
                --moveStep;
        } else if (angle >= 22.5 && angle < 67.5) {
        	dir = Direction.SouthEast;
            canWalk = map.isCanWalk(me.getX() + 1, me.getY() + 1);
            if(moveStep == 2 && !map.isCanWalk(me.getX() + 2, me.getY() + 2))
                --moveStep;
        } else if (angle >= 67.5 && angle < 112.5) {
        	dir = Direction.South;
            canWalk = map.isCanWalk(me.getX(), me.getY() + 1);
            if(moveStep == 2 && !map.isCanWalk(me.getX(), me.getY() + 2))
                --moveStep;
        } else if (angle >= 112.5 && angle < 157.5) {
        	dir = Direction.SouthWest;
            canWalk = map.isCanWalk(me.getX() - 1, me.getY() + 1);
            if(moveStep == 2 && !map.isCanWalk(me.getX() - 2, me.getY() + 2))
                --moveStep;
        } else if (angle >= 157.5 && angle < 202.5) {
        	dir = Direction.West;
            canWalk = map.isCanWalk(me.getX() - 1, me.getY());
            if(moveStep == 2 && !map.isCanWalk(me.getX() - 2, me.getY()))
                --moveStep;
        } else if (angle >= 202.5 && angle < 247.5) {
        	dir = Direction.NorthWest;
            canWalk = map.isCanWalk(me.getX() - 1, me.getY() - 1);
            if(moveStep == 2 && !map.isCanWalk(me.getX() - 2, me.getY() - 2))
                --moveStep;
        } else if (angle >= 247.5 && angle < 292.5) {
        	dir = Direction.North;
        	canWalk = map.isCanWalk(me.getX(), me.getY() - 1);
            if(moveStep == 2 && !map.isCanWalk(me.getX(), me.getY() - 2))
                --moveStep;
        } else if (angle >= 292.5 && angle < 337.5) {
        	dir = Direction.NorthEast;
        	 canWalk = map.isCanWalk(me.getX() + 1, me.getY() - 1);
             if(moveStep == 2 && !map.isCanWalk(me.getX() + 2, me.getY() - 2))
                 --moveStep;
        }
        
        if (canWalk) {
	        if (moveStep == 1) {
	        	return HumActionInfos.walk(dir);
	        } else if (moveStep == 2) {
	        	return HumActionInfos.run(dir);
	        }
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
		@Override
		public boolean keyUp(InputEvent event, int keycode) {
			return JootM2C.this.mapKeyUp(event, keycode);
		}
	}

}