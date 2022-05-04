package joot.m2.client;

import java.util.Map;

import com.badlogic.gdx.Game;
import com.github.jootnet.m2.core.actor.ChrBasicInfo;
import com.github.jootnet.m2.core.actor.ChrPrivateInfo;
import com.github.jootnet.m2.core.actor.ChrPublicInfo;
import com.github.jootnet.m2.core.net.messages.LoginResp;

import joot.m2.client.scene.ChrSelScene;
import joot.m2.client.scene.GameScene;
import joot.m2.client.scene.LoginScene;

/**
 * 游戏入口
 * 
 * @author linxing
 *
 */
public class App extends Game {
	private static App me = null;
	/** 账号已有的角色列表 */
	public static LoginResp.Role[] Roles;
	/** 上次登录的昵称 */
	public static String LastName;
	/** 地图编号 */
	public static String MapNo;
	/** 与服务器时间差（秒） */
	public static long timeDiff;
	// 地图信息
	public static Map<String, String> MapNames;
	public static Map<String, Integer> MapMMaps;
	// 当前进入游戏的角色信息
	public static ChrBasicInfo ChrBasic;
	public static ChrPublicInfo ChrPublic;
	public static ChrPrivateInfo ChrPrivate;
	/**
	 * 是否平滑移动
	 */
	public static boolean SmoothMoving = false;
	
	// 登陆场景
	private LoginScene loginScene;
	// 角色选择场景
	private ChrSelScene chrSelScene;
	// 游戏场景
	private GameScene gameScene;

	public App() {
		me = this;
	}
	
	@Override
	public void create() {
		loginScene = new LoginScene();
		chrSelScene = new ChrSelScene();
		gameScene = new GameScene();
		setScreen(loginScene);
	}

	/**
	 * 去到登陆界面
	 */
	public static void toLogin() {
		me.setScreen(me.loginScene);
	}
	
	/**
	 * 去到角色选择场景
	 */
	public static void toChrSel() {
		me.setScreen(me.chrSelScene);
	}
	
	/**
	 * 去到游戏场景
	 */
	public static void toGame() {
		me.setScreen(me.gameScene);
	}
}
