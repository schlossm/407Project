package uikit;

import java.awt.*;
import java.io.IOException;

import static java.awt.Font.createFont;

@SuppressWarnings("unused")
public class UIFont
{
	//Display.  Size 32+

	public static Font displayBlack;
	public static Font displayBold;
	public static Font displayHeavy;
	public static Font displayLight;
	public static Font displayMedium;
	public static Font displayRegular;
	public static Font displaySemibold;
	public static Font displayThin;
	public static Font displayUltralight;

	//Text.  Size 31-

	public static Font textBold;
	public static Font textBoldItalic;
	public static Font textHeavy;
	public static Font textHeavyItalic;
	public static Font textLight;
	public static Font textLightItalic;
	public static Font textMedium;
	public static Font textMediumItalic;
	public static Font textRegular;
	public static Font textRegularItalic;
	public static Font textSemibold;
	public static Font textSemiboldItalic;
	public static Font textUltrathin;
	public static Font textUltrathinItalic;

	public static void loadIntoGE()
	{
		try
		{
			displayBlack = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Black.otf"));
			displayBold = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Bold.otf"));
			displayHeavy = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Heavy.otf"));
			displayLight = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Light.otf"));
			displayMedium = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Medium.otf"));
			displayRegular = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Regular.otf"));
			displaySemibold = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Semibold.otf"));
			displayThin = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Thin.otf"));
			displayUltralight = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Display-Ultralight.otf"));

			textBold = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Bold.otf"));
			textBoldItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-BoldItalic.otf"));
			textHeavy = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Heavy.otf"));
			textHeavyItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-HeavyItalic.otf"));
			textLight = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Light.otf"));
			textLightItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-LightItalic.otf"));
			textMedium = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Medium.otf"));
			textMediumItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-MediumItalic.otf"));
			textRegular = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Regular.otf"));
			textRegularItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-RegularItalic.otf"));
			textSemibold = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Semibold.otf"));
			textSemiboldItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-SemiboldItalic.otf"));
			textUltrathin = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-Ultrathin.otf"));
			textUltrathinItalic = createFont(Font.TRUETYPE_FONT, UIFont.class.getResourceAsStream("SF-UI-Text-UltrathinItalic.otf"));
		}
		catch (FontFormatException | IOException e)
		{
			e.printStackTrace();
		}

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		ge.registerFont(UIFont.displayBlack);
		ge.registerFont(UIFont.displayBold);
		ge.registerFont(UIFont.displayHeavy);
		ge.registerFont(UIFont.displayLight);
		ge.registerFont(UIFont.displayMedium);
		ge.registerFont(UIFont.displayRegular);
		ge.registerFont(UIFont.displaySemibold);
		ge.registerFont(UIFont.displayThin);
		ge.registerFont(UIFont.displayUltralight);

		ge.registerFont(UIFont.textBold);
		ge.registerFont(UIFont.textHeavy);
		ge.registerFont(UIFont.textLight);
		ge.registerFont(UIFont.textMedium);
		ge.registerFont(UIFont.textRegular);
		ge.registerFont(UIFont.textSemibold);
		ge.registerFont(UIFont.textUltrathin);
		ge.registerFont(UIFont.textBoldItalic);
		ge.registerFont(UIFont.textHeavyItalic);
		ge.registerFont(UIFont.textLightItalic);
		ge.registerFont(UIFont.textMediumItalic);
		ge.registerFont(UIFont.textRegularItalic);
		ge.registerFont(UIFont.textSemiboldItalic);
		ge.registerFont(UIFont.textUltrathinItalic);
	}
}
