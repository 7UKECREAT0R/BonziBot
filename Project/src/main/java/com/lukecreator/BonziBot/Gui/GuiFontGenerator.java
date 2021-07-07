package com.lukecreator.BonziBot.Gui;

import com.lukecreator.BonziBot.GuiAPI.GuiPaging;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;

public class GuiFontGenerator extends GuiPaging {
	
	static final char[] base = "qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM1234567890".toCharArray();
	
	class Font {
		String name;
		char[] map;
		
		Font(String name, String map) {
			this.name = name;
			this.map = map.toCharArray();
		}
		String remap(String old) {
			if(old == null)
				return null;
			char[] chars = old.toCharArray();
			for(int i = 0; i < chars.length; i++) {
				char cur = chars[i];
				int indexOf = -1;
				for(int x = 0; x < base.length; x++) {
					if(cur == base[x]) {
						indexOf = x;
						break;
					}
				}
				if(indexOf == -1)
					continue;
				chars[i] = map[indexOf];
			}
			return new String(chars);
		}
	}
	
	Font[] fonts = new Font[] {
		new Font("Circular", "ⓠⓦⓔⓡⓣⓨⓤⓘⓞⓟⓐⓢⓓⓕⓖⓗⓙⓚⓛⓩⓧⓒⓥⓑⓝⓜⓆⓌⒺⓇⓉⓎⓊⒾⓄⓅⒶⓈⒹⒻⒼⒽⒿⓀⓁⓏⓍⒸⓋⒷⓃⓂ①②③④⑤⑥⑦⑧⑨⓪"),
		new Font("All Caps", "ǫᴡᴇʀᴛʏᴜɪᴏᴘᴀsᴅғɢʜᴊᴋʟᴢxᴄᴠʙɴᴍǫᴡᴇʀᴛʏᴜɪᴏᴘᴀsᴅғɢʜᴊᴋʟᴢxᴄᴠʙɴᴍ𝟷𝟸𝟹𝟺𝟻𝟼𝟽𝟾𝟿𝟶"),
		new Font("Slipknot", "Q₩ɆⱤ₮ɎɄłØ₱₳₴Đ₣₲ⱧJ₭ⱠⱫӾ₵V฿₦₥Q₩ɆⱤ₮ɎɄłØ₱₳₴Đ₣₲ⱧJ₭ⱠⱫӾ₵V฿₦₥1234567890"),
		new Font("Souvlaki", "qωєятуυισραѕ∂ƒgнנкℓzχ¢νвηмqωєятуυισραѕ∂ƒgнנкℓzχ¢νвηм1234567890"),
		new Font("Wasal", "Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪Ɋ山乇尺ㄒㄚㄩ丨ㄖ卩卂丂ᗪ千Ꮆ卄ﾌҜㄥ乙乂匚ᐯ乃几爪1234567890"),
		new Font("Notebook", "ᑫᗯEᖇTYᑌIOᑭᗩᔕᗪᖴGᕼᒍKᒪᘔ᙭ᑕᐯᗷᑎᗰᑫᗯEᖇTYᑌIOᑭᗩᔕᗪᖴGᕼᒍKᒪᘔ᙭ᑕᐯᗷᑎᗰ1234567890"),
		new Font("Upside Down", "bʍǝɹʇʎnıodɐspɟɓɥɾʞlzxɔʌquɯΌMƎᴚ⊥⅄∩IOԀ∀SᗡℲ⅁Hſ⋊˥ZXƆΛᙠNW⇂ᄅƐㄣގ9ㄥ860"),
		new Font("Lite", "ｑｗｅｒｔｙｕｉｏｐａｓｄｆｇｈｊｋｌｚｘｃｖｂｎｍＱＷＥＲＴＹＵＩＯＰＡＳＤＦＧＨＪＫＬＺＸＣＶＢＮＭ１２３４５６７８９０"),
		new Font("Funky Bunch", "զաҽɾէվմìօքąʂժƒցհʝҟӀՀ×çѵҍղʍҨచƐའͲӋԱįටφȺϚᎠƑƓǶلҠꝈɀჯ↻ỼβហⱮ𝟙ϩӠ५ƼϬ7𝟠९⊘"),
		new Font("Slicer", "ꝗwɇɍŧɏᵾɨøᵽȺsđfǥħɉꝁłƶxȼvƀnmꝖWɆɌŦɎᵾƗØⱣȺSĐFǤĦɈꝀŁƵXȻVɃNM1ƻ34567890"),
		new Font("Pound Cake", "ʠⱳҽɾƭყʋíօƥąʂɗƒɠɦᴊƙƖzxƈⱱɓղɱQⱲҼRƬƳƲӀƠƤⱭՏƊƑƓӇJƘlȤXƇVƁƝⱮ1234567890"),
		new Font("Gawd Mode", "ƢƜЄƦƬƳƲƖƠƤƛƧƊƑƓӇʆƘԼȤҲƇƔƁƝMƢƜЄƦƬƳƲƖƠƤƛƧƊƑƓӇʆƘԼȤҲƇƔƁƝM1234567890"),
		new Font("Wiggle Arms", "ᑫᙎᙓᖇTYᙀIOᑭᗩSᗪᖴᘜᕼᒍKᒪᘔ᙭ᙅᐯᙖᑎᙏᑫᙎᙓᖇTYᙀIOᑭᗩSᗪᖴᘜᕼᒍKᒪᘔ᙭ᙅᐯᙖᑎᙏ1234567890"),
		new Font("Wonky Doodle", "ợฬєгtץยเ๏թคร๔Ŧɠђןкlzxςv๒ภ๓ợฬєгtץยเ๏թคร๔Ŧɠђןкlzxςv๒ภ๓1234567890"),
		new Font("Quebec", "qաҽɾեყմíօթɑsժƒցհᴊƙƖzxϲѵҍղʍQⱲҼRƬƳƲӀƠƤⱭՏƊƑƓӇJƘlȤXƇVƁƝⱮ1234567890"),
		new Font("Spicy ABC", "qᾧἔʀҭẏὗἷὄῥᾄṩḋғʛђʝќłẓẋƈvвᾗмQẂἝȒҬὛȖἿὋƤᾋṨƉҒƓἬЈḰĿẔẊƇVϐƝṂ1234567890"),
		new Font("Impression", "qŵêѓţŷʉĩǿpãşdfĝĥĵklżxĉvbñmQWERTYUIOPASDFGHJKLZXCVBNM1234567890"),
		new Font("WiFi", "qᾧἔʀҭẏὗἷὄῥᾄṩḋғʛђʝќłẓẋƈvвᾗмQẂἝȒҬὛȖἿὋƤᾋṨƉҒƓἬЈḰĿẔẊƇVϐƝṂ1234567890"),
		new Font("Short Caps", "qwerтyυιopαѕdғɢнjĸlzхcvвɴмQWERTYUIOPASDFGHJKLZXCVBNM1234567890"),
		new Font("Fancy Text", "qωєятуυισραѕ∂ƒgнנкℓzχ¢νвηмQWERTYUIOPASDFGHJKLZXCVBNM1234567890")
	};
	
	public String text;
	public GuiFontGenerator(String text) {
		this.text = text;
	}
	
	@Override
	public void initialize(JDA jda) {
		super.initialize(jda);
		this.currentPage = 1;
		this.maxPage = this.fonts.length;
		this.minPage = 1;
	}
	
	@Override
	public Object draw(JDA jda) {
		Font selected = this.fonts[this.currentPage - 1];
		String display = selected.remap(this.text);
		EmbedBuilder eb = new EmbedBuilder()
			.setTitle("Font: " + selected.name)
			.setDescription("```" + this.text +
				"```\n```" + display + "```")
			.setFooter("Font " + this.currentPage + "/" + this.maxPage);
		return eb.build();
	}
}
