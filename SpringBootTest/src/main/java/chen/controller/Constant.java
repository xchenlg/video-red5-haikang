package chen.controller;

import java.io.*;
import java.util.Properties;

/**
 * @author jijs 2017/1/17
 */
public class Constant {

    public static int port = 38000;

    public static String httpServer = "http://127.0.0.1:"+port+"/";


    public static String getRootPath() {
//        return MediaUtils.getNextDir() + "whjz";
        return "whjz";
    }

//    public static void main(String[] args) {
//        System.out.println(getRootPath());
//    }

    public static String[] titles = new String[]{
            "第一期：张国刚：比较文明视野下中国历史与文化特色",
            "第二期：孙机：古代中国 一个充满创造力的国家",
            "第三期：厉声：“丝绸之路”文化的叠压式传承与对接",
            "第四期：周育德：东方的莎士比亚——汤显祖",
            "第五期：齐大辉：家风文化 家国情怀",
            "第六期：朱孝远：文化兴国的欧洲经验",
            "第七期：姚大力：内陆亚洲与中国历史",
            "第八期：牟钟鉴：孔子儒学与重铸君子人格",
            "第九期：金寿福：埃及文明的再发现",
            "第十期：晏绍祥：罗马：从城邦到帝国",
            "第十一期：郭齐勇：王阳明的人生与思想智慧",
            "第十二期：张宇燕：全球治理的中国视角"};

    public static String[] userInfos = new String[]{
            "张国刚",
            "孙机",
            "厉声",
            "周育德",
            "齐大辉",
            "朱孝远",
            "姚大力",
            "牟钟鉴",
            "金寿福",
            "晏绍祥",
            "郭齐勇",
            "张宇燕"};

    public static String[] subtitles = new String[]{
            "比较文明视野下中国历史与文化特色",
            "古代中国 一个充满创造力的国家",
            "“丝绸之路”文化的叠压式传承与对接",
            "东方的莎士比亚——汤显祖",
            "家风文化 家国情怀",
            "文化兴国的欧洲经验",
            "内陆亚洲与中国历史",
            "孔子儒学与重铸君子人格",
            "埃及文明的再发现",
            "罗马：从城邦到帝国",
            "王阳明的人生与思想智慧",
            "全球治理的中国视角"
    };

    public static String[] digests = new String[]{
            "张国刚，1956年生，安徽安庆人。现任清华大学历史系教授、博士生导师。张国刚先生主要致力于中国古代史、中西文化交流史的研究，特别是在隋唐史、欧洲汉学史、中西文化交流史方面造诣颇深，成就斐然。",
            "孙机，山东青岛人，1929年9月生。现为中国国家博物馆研究馆员、国家文物鉴定委员会副主任委员、中央文史研究馆馆员、全国古籍整理出版规划领导小组成员，《文物》、《中国历史文物》、《收藏家》编委。1992年获国务院特殊津贴，2008年获得中国美术家协会授予的“卓有成就的美术史论家”奖。曾任第四、六届国家图书奖评委，第一届政府图书奖评委。孙机先生长期从事古代车制、中国古代服饰史、中国印刷术、科技史等研究。主要著作有：《汉代物质文化资料图说》、《中国古舆服论丛》、《孙机谈文物》、《中国古代物质文化》等。其中，《汉代物质文化资料图说》一书被誉为一部百科式的、足以代表汉代物质文化全貌的煌煌大作。",
            "厉声，1949年8月生于北京。中共党员。1985年毕业于西北大学西北历史研究室，获硕士学位。1993年～1997年任新疆大学历史系主任、教授。1997年11月调入中国社会科学院中国边疆史地研究中心，任中心副主任。2001至2012年任中国边疆史地研究中心主任。现任中国社会科学院中国边疆史地研究中心研究员、博士生导师，获得国务院颁发的政府特殊津贴。兼任中国中亚文化研究会秘书长、中国中俄关系史学会副会长、中国中外关系史学会副会长、武汉大学边界研究院兼职研究员、中国人民大学兼职教授、南京大学兼职教授、中央民族大学大学兼职教授、吉林大学兼职教授、新疆大学兼职教授、云南大学兼职教授、内蒙古师范大学兼职教授。主要研究方向：中国疆域史、新疆地方历史、中亚近现代历史。",
            "周育德，1938年11月，出生于山东省平度城。1961年7月，毕业于杭州大学（现浙江大学）中国语言文学系，后就读于中国艺术研究院。曾任中国艺术研究院研究员、研究生部副主任、中国戏曲学院院长、研究员。多年来从事戏曲历史及理论的研究与教学，并从事戏曲文献的整理与文化人类学研究。发表过学术论文多篇，出版过学术著作多种。参加过多种丛书、辞书和志书的编写，在学术界和教育界有良好的声誉和影响。享受国务院政府特殊津贴。著有《中国戏曲与中国宗教》《汤显祖论稿》等著作，并有多篇有影响力的文章在《文艺研究》、《学术研究》、《戏剧艺术》、《艺术百家》、《民俗曲艺》等刊物进行发表。",
            "齐大辉，1961年5月出生于中国大连。现任北京大学文化研究与发展中心研究员，北京大学汇丰商学院EMBA后财富中心主任教授，北京大学家庭文化与家长教育研究所所长，北京书同教育科技研究院院长。兼任北京师范大学家庭教育研究中心副主任，北京市家庭教育研究会副会长，北京市教育学专家委员会副主任，北京市教工委专家咨询委员会委员、家长教育、社区教育顾问。齐大辉先生是中国家长教育学术的带头人；国民素质从娃娃抓起、娃娃素质从家长抓起，抓党建促家建、正家风助党风“源头”教育思想的提出者。主要研究领域：群众工作创新、社会情绪管理、家庭科学研究、家风公约文化，以及家长科普教育等。相关著作有：《爱是一次共同的成长》、《新形势下做好群众工作的艺术与方法创新》（合著）等。其中，《新形势下做好群众工作的艺术与方法创新》于2012年被中组部评为全国21本优秀党建读物。",
            "朱孝远，1954年生。北京大学历史学系教授，美国俄勒冈大学荣誉教授，希腊雅典荣誉公民钥匙获得者，北京大学世界文化史专业博士生导师，北京市高等教育教学名师,中国世界中世纪史学会副会长。曾获国际奖4项，国家级奖8项，省部级奖10项，其他奖7项。长期从事欧洲文化史、德国史、史学理论研究。出版作品26部，含著作12部，合著2部，参与翻译、审校译著2部，主编、校审世界经典译著10部。",
            "姚大力，1949年生于上海。现为复旦大学历史地理研究所教授、博士生导师，复旦大学文科特聘资深教授，并任清华大学国学研究院特聘兼职教授。姚大力先生主要研究方向为元朝史、中国边疆史地。曾先后发表论文及学术评论近百篇，部分结集为《北方民族史十论》、《蒙元制度与政治文化》、《读史的智慧》、《追寻“我们”的根源：传统中国的民族与国家认同》等。",
            "牟钟鉴，1939年生，山东烟台人。北京大学哲学系中国哲学史专业研究生毕业。1966年4月至1987年11月任职于中国社会科学院世界宗教研究所。1987年底调入中央民族大学哲学与宗教学学院，担任教授、博士生导师。曾获全国优秀教师称号，全国民族团结进步模范个人荣誉称号，并在第五届世界儒学大会上，获2012年度孔子文化奖个人奖。 兼任中国宗教学会顾问、国际儒学联合会副会长、国家行政学院兼职教授、中央统战部专家咨询组成员、国家宗教事务局宗教工作专家库特聘专家、中国统一战线理论研究会民族宗教理论甘肃研究基地研究员、孔子研究院（设在曲阜）学术委员会主任、山东泗水尼山圣源书院荣誉院长等。 牟钟鉴先生早年研究重点为儒学、道家与道教、儒佛道三教关系，近年来转为中国宗教史、民族与宗教、社会主义与宗教。主要著作有《中国道教》、《走近中国精神》、《儒学价值的新探索》、《探索宗教》、《老子新说》、《中国宗教通史》（与张践合写，获第三届中国高校人文社会科学研究优秀成果奖宗教学一等奖）、《涵泳儒学》、《在国学的路上》、《新仁学构想》、《道家和道教论稿》等。",
            "金寿福，1964年生，埃及学博士。现任首都师范大学历史学院教授、博士生导师，中国世界古代中世纪史研究会古代史专业委员会副会长。曾任复旦大学历史系教授、埃及研究中心主任。2013年，被评为北京市特聘教授，同年入选国家百千万人才工程，被授予“有突出贡献中青年专家”荣誉称号；2016被评为国家“万人计划”领军人才。金寿福先生主要从事古代埃及历史、古代文明比较、文化记忆等领域的研究。有多部著作和译著：布克哈特《世界历史沉思录》、阿斯曼的《文化记忆》等。并在《中国社会科学》等刊物发表论文二十多篇，以及美国、德国等国的埃及学专业杂志上发表论文近十篇 。同时，还承担国家社科基金项目《古代埃及官吏制度》和北京市人才项目《古代埃及亡灵书研究》，已完成国家社科后期资助项目《古代埃及亡灵书》。",
            "晏绍祥，安徽金寨人，现为首都师范大学历史学院教授、博士生导师、教育部长江学者特聘教授，兼任中国世界古代中世纪史研究会副会长兼秘书长，古代史专业委员会会长，《史学理论研究》编委等。晏绍祥主要研究领域为西方古典文明、古希腊罗马史、古典传统与西方政治思想等，先后出版《古典历史研究史》、《古典民主与共和传统》等专著，独立撰写大学教材《世界上古史》等；译著有《罗马共和国政制》、《罗马的遗产》等近10种。在《历史研究》、《世界历史》等刊物发表论文近百篇。主持的研究课题包括国家社科基金项目“古希腊史研究”、“西方历史中的古典民主传统”；教育部社科规划项目“古典世界的民主与共和政治”“荷马社会研究”等；专著《古典历史研究发展史》和《古典民主与共和传统》等先后获得教育部高等学校人文社会科学研究优秀成果奖。",
            "郭齐勇，哲学博士。现为武汉大学哲学学院及国学院教授、博士生导师、国学院院长、武汉大学珞珈杰出学者，2006年被评为国家级教学名师。长期从事中国哲学与文化的教学研究工作，重点领域为中国哲学史与儒学史，是国家重点学科“武汉大学中国哲学学科”学术带头人。主要著作有：《中国哲学史》、《中国儒学之精神》、《中国哲学智慧的探索》、《中华人文精神的重建》、《儒学与现代化的新探讨》、《守先待后》、《熊十力哲学研究》等。",
            "张宇燕，中国社会科学院世界经济与政治研究所研究员、所长，中国社会科学院研究生院教张宇燕，中国社会科学院世界经济与政治研究所研究员、所长，中国社会科学院研究生院教授，博士生导师。1983年毕业于北京大学经济系世界经济专业，获得经济学学士；1986年和1991年分别在中国社会科学院研究生院世界经济与政治系，获得经济学硕士和经济学博士。曾任中国社会科学院院长学术秘书，中国社会科学院美国研究所副所长，中国社会科学院亚洲太平洋研究所所长、党委书记。外交部政策咨询委员会委员，商务部经贸政策咨询委员会专家，中国世界经济学会会长，中国新兴经济体研究会会长。张宇燕先生长期从事制度经济学和国际政治经济学的研究,著有《美国行为根源》、《经济发展与制度选择》、《国际经济政治学》、《键盘上的经济学》等。"
    };

    public static void readConf(){
        Properties prop = new Properties();
        try {
            //读取属性文件a.properties
            Reader in = new InputStreamReader(Constant.class.getResourceAsStream("/conf.properties"), "UTF-8");
            prop.load(in);     ///加载属性列表

            String title1 = prop.getProperty("title1");
            String title2 = prop.getProperty("title2");
            String title3 = prop.getProperty("title3");
            String title4 = prop.getProperty("title4");
            String title5 = prop.getProperty("title5");
            String title6 = prop.getProperty("title6");
            String title7 = prop.getProperty("title7");
            String title8 = prop.getProperty("title8");
            String title9 = prop.getProperty("title9");
            String title10 = prop.getProperty("title10");
            String title11 = prop.getProperty("title11");
            String title12 = prop.getProperty("title12");
            if(isNotEmpty(title1))  titles[0] = title1;
            if(isNotEmpty(title2))  titles[1] = title2;
            if(isNotEmpty(title3))  titles[2] = title3;
            if(isNotEmpty(title4))  titles[3] = title4;
            if(isNotEmpty(title5))  titles[4] = title5;
            if(isNotEmpty(title6))  titles[5] = title6;
            if(isNotEmpty(title7))  titles[6] = title7;
            if(isNotEmpty(title8))  titles[7] = title8;
            if(isNotEmpty(title9))  titles[8] = title9;
            if(isNotEmpty(title10)) titles[9] = title10;
            if(isNotEmpty(title11)) titles[10] = title11;
            if(isNotEmpty(title12)) titles[11] = title12;

            String userInfo1 = prop.getProperty("userInfo1");
            String userInfo2 = prop.getProperty("userInfo2");
            String userInfo3 = prop.getProperty("userInfo3");
            String userInfo4 = prop.getProperty("userInfo4");
            String userInfo5 = prop.getProperty("userInfo5");
            String userInfo6 = prop.getProperty("userInfo6");
            String userInfo7 = prop.getProperty("userInfo7");
            String userInfo8 = prop.getProperty("userInfo8");
            String userInfo9 = prop.getProperty("userInfo9");
            String userInfo10 = prop.getProperty("userInfo10");
            String userInfo11 = prop.getProperty("userInfo11");
            String userInfo12 = prop.getProperty("userInfo12");
            if(isNotEmpty(userInfo1))   userInfos[0] = userInfo1;
            if(isNotEmpty(userInfo2))   userInfos[1] = userInfo2;
            if(isNotEmpty(userInfo3))   userInfos[2] = userInfo3;
            if(isNotEmpty(userInfo4))   userInfos[3] = userInfo4;
            if(isNotEmpty(userInfo5))   userInfos[4] = userInfo5;
            if(isNotEmpty(userInfo6))   userInfos[5] = userInfo6;
            if(isNotEmpty(userInfo7))   userInfos[6] = userInfo7;
            if(isNotEmpty(userInfo8))   userInfos[7] = userInfo8;
            if(isNotEmpty(userInfo9))   userInfos[8] = userInfo9;
            if(isNotEmpty(userInfo10))  userInfos[9] = userInfo10;
            if(isNotEmpty(userInfo11))  userInfos[10] = userInfo11;
            if(isNotEmpty(userInfo12))  userInfos[11] = userInfo12;

            String subtitle1 = prop.getProperty("subtitle1");
            String subtitle2 = prop.getProperty("subtitle2");
            String subtitle3 = prop.getProperty("subtitle3");
            String subtitle4 = prop.getProperty("subtitle4");
            String subtitle5 = prop.getProperty("subtitle5");
            String subtitle6 = prop.getProperty("subtitle6");
            String subtitle7 = prop.getProperty("subtitle7");
            String subtitle8 = prop.getProperty("subtitle8");
            String subtitle9 = prop.getProperty("subtitle9");
            String subtitle10 = prop.getProperty("subtitle10");
            String subtitle11 = prop.getProperty("subtitle11");
            String subtitle12 = prop.getProperty("subtitle12");
            if(isNotEmpty(subtitle1))   subtitles[0] = subtitle1;
            if(isNotEmpty(subtitle2))   subtitles[1] = subtitle2;
            if(isNotEmpty(subtitle3))   subtitles[2] = subtitle3;
            if(isNotEmpty(subtitle4))   subtitles[3] = subtitle4;
            if(isNotEmpty(subtitle5))   subtitles[4] = subtitle5;
            if(isNotEmpty(subtitle6))   subtitles[5] = subtitle6;
            if(isNotEmpty(subtitle7))   subtitles[6] = subtitle7;
            if(isNotEmpty(subtitle8))   subtitles[7] = subtitle8;
            if(isNotEmpty(subtitle9))   subtitles[8] = subtitle9;
            if(isNotEmpty(subtitle10))  subtitles[9] = subtitle10;
            if(isNotEmpty(subtitle11))  subtitles[10] = subtitle11;
            if(isNotEmpty(subtitle12))  subtitles[11] = subtitle12;

            String digest1 = prop.getProperty("digest1");
            String digest2 = prop.getProperty("digest2");
            String digest3 = prop.getProperty("digest3");
            String digest4 = prop.getProperty("digest4");
            String digest5 = prop.getProperty("digest5");
            String digest6 = prop.getProperty("digest6");
            String digest7 = prop.getProperty("digest7");
            String digest8 = prop.getProperty("digest8");
            String digest9 = prop.getProperty("digest9");
            String digest10 = prop.getProperty("digest10");
            String digest11 = prop.getProperty("digest11");
            String digest12 = prop.getProperty("digest12");
            if(isNotEmpty(digest1))     digests[0] = digest1;
            if(isNotEmpty(digest2))     digests[1] = digest2;
            if(isNotEmpty(digest3))     digests[2] = digest3;
            if(isNotEmpty(digest4))     digests[3] = digest4;
            if(isNotEmpty(digest5))     digests[4] = digest5;
            if(isNotEmpty(digest6))     digests[5] = digest6;
            if(isNotEmpty(digest7))     digests[6] = digest7;
            if(isNotEmpty(digest8))     digests[7] = digest8;
            if(isNotEmpty(digest9))     digests[8] = digest9;
            if(isNotEmpty(digest10))    digests[9] = digest10;
            if(isNotEmpty(digest11))    digests[10] = digest11;
            if(isNotEmpty(digest12))    digests[11] = digest12;

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isNotEmpty(String s){
        if(s==null || "".equals(s.trim())){
            return false;
        }
        return true;
    }
}
