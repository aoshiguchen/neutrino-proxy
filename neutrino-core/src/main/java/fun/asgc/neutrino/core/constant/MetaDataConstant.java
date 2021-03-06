/**
 * Copyright (c) 2022 aoshiguchen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package fun.asgc.neutrino.core.constant;

/**
 *
 * @author: aoshiguchen
 * @date: 2022/6/16
 */
public interface MetaDataConstant {

	String DEFAULT_BANNER = "\n          _____                    _____                    _____                _____                    _____                    _____                    _____                   _______         \n" +
		"         /\\    \\                  /\\    \\                  /\\    \\              /\\    \\                  /\\    \\                  /\\    \\                  /\\    \\                 /::\\    \\        \n" +
		"        /::\\____\\                /::\\    \\                /::\\____\\            /::\\    \\                /::\\    \\                /::\\    \\                /::\\____\\               /::::\\    \\       \n" +
		"       /::::|   |               /::::\\    \\              /:::/    /            \\:::\\    \\              /::::\\    \\               \\:::\\    \\              /::::|   |              /::::::\\    \\      \n" +
		"      /:::::|   |              /::::::\\    \\            /:::/    /              \\:::\\    \\            /::::::\\    \\               \\:::\\    \\            /:::::|   |             /::::::::\\    \\     \n" +
		"     /::::::|   |             /:::/\\:::\\    \\          /:::/    /                \\:::\\    \\          /:::/\\:::\\    \\               \\:::\\    \\          /::::::|   |            /:::/~~\\:::\\    \\    \n" +
		"    /:::/|::|   |            /:::/__\\:::\\    \\        /:::/    /                  \\:::\\    \\        /:::/__\\:::\\    \\               \\:::\\    \\        /:::/|::|   |           /:::/    \\:::\\    \\   \n" +
		"   /:::/ |::|   |           /::::\\   \\:::\\    \\      /:::/    /                   /::::\\    \\      /::::\\   \\:::\\    \\              /::::\\    \\      /:::/ |::|   |          /:::/    / \\:::\\    \\  \n" +
		"  /:::/  |::|   | _____    /::::::\\   \\:::\\    \\    /:::/    /      _____        /::::::\\    \\    /::::::\\   \\:::\\    \\    ____    /::::::\\    \\    /:::/  |::|   | _____   /:::/____/   \\:::\\____\\ \n" +
		" /:::/   |::|   |/\\    \\  /:::/\\:::\\   \\:::\\    \\  /:::/____/      /\\    \\      /:::/\\:::\\    \\  /:::/\\:::\\   \\:::\\____\\  /\\   \\  /:::/\\:::\\    \\  /:::/   |::|   |/\\    \\ |:::|    |     |:::|    |\n" +
		"/:: /    |::|   /::\\____\\/:::/__\\:::\\   \\:::\\____\\|:::|    /      /::\\____\\    /:::/  \\:::\\____\\/:::/  \\:::\\   \\:::|    |/::\\   \\/:::/  \\:::\\____\\/:: /    |::|   /::\\____\\|:::|____|     |:::|    |\n" +
		"\\::/    /|::|  /:::/    /\\:::\\   \\:::\\   \\::/    /|:::|____\\     /:::/    /   /:::/    \\::/    /\\::/   |::::\\  /:::|____|\\:::\\  /:::/    \\::/    /\\::/    /|::|  /:::/    / \\:::\\    \\   /:::/    / \n" +
		" \\/____/ |::| /:::/    /  \\:::\\   \\:::\\   \\/____/  \\:::\\    \\   /:::/    /   /:::/    / \\/____/  \\/____|:::::\\/:::/    /  \\:::\\/:::/    / \\/____/  \\/____/ |::| /:::/    /   \\:::\\    \\ /:::/    /  \n" +
		"         |::|/:::/    /    \\:::\\   \\:::\\    \\       \\:::\\    \\ /:::/    /   /:::/    /                 |:::::::::/    /    \\::::::/    /                   |::|/:::/    /     \\:::\\    /:::/    /   \n" +
		"         |::::::/    /      \\:::\\   \\:::\\____\\       \\:::\\    /:::/    /   /:::/    /                  |::|\\::::/    /      \\::::/____/                    |::::::/    /       \\:::\\__/:::/    /    \n" +
		"         |:::::/    /        \\:::\\   \\::/    /        \\:::\\__/:::/    /    \\::/    /                   |::| \\::/____/        \\:::\\    \\                    |:::::/    /         \\::::::::/    /     \n" +
		"         |::::/    /          \\:::\\   \\/____/          \\::::::::/    /      \\/____/                    |::|  ~|               \\:::\\    \\                   |::::/    /           \\::::::/    /      \n" +
		"         /:::/    /            \\:::\\    \\               \\::::::/    /                                  |::|   |                \\:::\\    \\                  /:::/    /             \\::::/    /       \n" +
		"        /:::/    /              \\:::\\____\\               \\::::/    /                                   \\::|   |                 \\:::\\____\\                /:::/    /               \\::/____/        \n" +
		"        \\::/    /                \\::/    /                \\::/____/                                     \\:|   |                  \\::/    /                \\::/    /                 ~~              \n" +
		"         \\/____/                  \\/____/                  ~~                                            \\|___|                   \\/____/                  \\/____/                                  \n" +
		"                                                                                                                                                                                                    ";

	/**
	 * classpath?????????
	 */
	String CLASSPATH_IDENTIFIER = "classpath";

	/**
	 * classpath????????????
	 */
	String CLASSPATH_RESOURCE_IDENTIFIER = CLASSPATH_IDENTIFIER + ":";

	/**
	 * ?????????yml???????????????
	 */
	String DEFAULT_YML_CONFIG_FILE_NAME = "application.yml";

	/**
	 * ??????banner??????
	 */
	String APP_BANNER_FILE_PATH = CLASSPATH_RESOURCE_IDENTIFIER.concat("/banner.txt");

	/**
	 * ?????????http????????????
	 */
	String DEFAULT_HTTP_PAGE_PATH = CLASSPATH_RESOURCE_IDENTIFIER.concat("/webapp/");
	/**
	 * ????????????
	 */
	String SERVER_VS = "Neutrino-1.0";
}
