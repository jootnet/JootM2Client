package ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;

import joot.m2.client.image.M2Texture;
import joot.m2.client.util.AssetUtil;
import joot.m2.client.util.FontUtil;

/**
 * 聊天框
 * 
 * @author linxing
 *
 */
public final class ChatBox extends WidgetGroup {
	private M2Texture newopui10; // 聊天框左上角
	private M2Texture newopui11; // 聊天框上边框
	private M2Texture newopui12; // 聊天框右上角
	private M2Texture newopui13; // 聊天框左边框
	private M2Texture newopui14; // 聊天框右边框
	private M2Texture newopui15; // 聊天框左下角
	private M2Texture newopui16; // 聊天框上边框
	private M2Texture newopui17; // 聊天框右下角
	
	private TextField txtChat;

	public ChatBox() {
		addActor((txtChat = new TextField("", new TextField.TextFieldStyle(FontUtil.font12, Color.BLACK, null, null, null))));
		txtChat.setPosition(16, 7);
		txtChat.setWidth(380);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		if (newopui10 == null) {
			if (AssetUtil.isLoaded("newopui/10")) {
				newopui10 = AssetUtil.get("newopui/10");
			}
		}
		if (newopui11 == null) {
			if (AssetUtil.isLoaded("newopui/11")) {
				newopui11 = AssetUtil.get("newopui/11");
			}
		}
		if (newopui12 == null) {
			if (AssetUtil.isLoaded("newopui/12")) {
				newopui12 = AssetUtil.get("newopui/12");
			}
		}
		if (newopui13 == null) {
			if (AssetUtil.isLoaded("newopui/13")) {
				newopui13 = AssetUtil.get("newopui/13");
			}
		}
		if (newopui14 == null) {
			if (AssetUtil.isLoaded("newopui/14")) {
				newopui14 = AssetUtil.get("newopui/14");
			}
		}
		if (newopui15 == null) {
			if (AssetUtil.isLoaded("newopui/15")) {
				newopui15 = AssetUtil.get("newopui/15");
			}
		}
		if (newopui16 == null) {
			if (AssetUtil.isLoaded("newopui/16")) {
				newopui16 = AssetUtil.get("newopui/16");
			}
		}
		if (newopui17 == null) {
			if (AssetUtil.isLoaded("newopui/17")) {
				newopui17 = AssetUtil.get("newopui/17");
			}
		}
		if (newopui11 != null) {
			// 绘制聊天框上边框
			for (var x = 0; x < getWidth(); x += newopui11.getWidth()) {
				var restWidth = getWidth() - x;
				batch.draw(newopui11, getX() + x, getHeight() - newopui11.getHeight(), 0, 0, (int) Math.min(newopui11.getWidth(), restWidth), newopui11.getHeight());
			}
		}
		// 聊天框左边框
		if (newopui13 != null) {
			// 高度是一定的，画两次就够了
			batch.draw(newopui13, getX(), 40);
			batch.draw(newopui13, getX(), 80);
		}
		// 聊天框右边框
		if (newopui14 != null) {
			batch.draw(newopui14, getX() + getWidth() - newopui14.getWidth(), 40);
			batch.draw(newopui14, getX() + getWidth() - newopui14.getWidth(), 80);
		}
		// 聊天框下边框
		if (newopui16 != null) {
			for (var x = 0; x < getWidth(); x += newopui16.getWidth()) {
				var restWidth = getWidth() - x;
				batch.draw(newopui16, getX() + x, 0, 0, 0, (int) Math.min(newopui16.getWidth(), restWidth), newopui16.getHeight());
			}
		}
		// 四个角
		batch.draw(newopui10, getX(), getHeight() - newopui10.getHeight());
		batch.draw(newopui12, getX() + getWidth() - newopui12.getWidth(), getHeight() - newopui12.getHeight());
		batch.draw(newopui15, getX(), 0);
		batch.draw(newopui17, getX() + getWidth() - newopui12.getWidth(), 0);
		
		
		super.draw(batch, parentAlpha);
	}
	
	@Override
	public void layout() {
		txtChat.setWidth(getWidth() - 30);
	}
}