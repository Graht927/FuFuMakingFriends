package cn.graht.utils;

import org.springframework.util.CollectionUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class RandomNicknameGenerator {
    public static void main(String[] args) {
        System.out.println(generateRandomNickname());
    }
    public static String generateRandomNickname() {
        // 形容词列表
        String[] adjectives = {
                "快乐的", "聪明的", "勇敢的", "神秘的", "疯狂的", "美丽的", "优雅的", "坚强的", "敏捷的", "温柔的",
                "欢乐的", "智慧的", "英俊的", "迷人的", "活泼的", "自信的", "热情的", "冷静的", "善良的", "勤奋的",
                "幽默的", "乐观的", "忠诚的", "诚实的", "可靠的", "细心的", "大胆的", "温柔的", "害羞的", "开朗的",
                "风度翩翩的", "才华横溢的", "文武双全的", "英姿飒爽的", "神采奕奕的", "才思敏捷的", "气宇轩昂的", "仪态万方的", "光彩照人的", "鹤立鸡群的",
                "温文尔雅的", "谈笑风生的", "举止大方的", "风趣幽默的", "豁达大度的", "笑容可掬的", "心地善良的", "沉着冷静的", "刚毅果敢的", "慷慨激昂的",
                "才华出众的", "博学多才的", "志存高远的", "心胸宽广的", "见多识广的", "从容不迫的", "足智多谋的", "妙语连珠的", "风华正茂的", "意气风发的",
                "玉树临风的", "卓尔不群的", "气贯长虹的", "豪情壮志的", "慷慨解囊的", "豁然开朗的", "才华横溢的", "妙手回春的", "一鸣惊人的", "文采飞扬的",
                "才思泉涌的", "文韬武略的", "英姿勃发的", "风流倜傥的", "博古通今的", "才华盖世的", "温润如玉的", "才貌双全的", "才思敏捷的", "才华出众的",
                "风华绝代的", "才情横溢的", "才华横溢的", "才思敏捷的", "才高八斗的", "才貌双全的", "才华横溢的", "才思敏捷的", "才情并茂的", "才情出众的",
                "才貌双全的", "才思敏捷的", "才情横溢的", "才华出众的", "才思敏捷的", "才情横溢的", "才华盖世的", "才思敏捷的", "才貌双全的", "才情出众的",
                "才思敏捷的", "才华横溢的", "才貌双全的", "才思敏捷的", "才情横溢的", "才华盖世的", "才思敏捷的", "才貌双全的", "才情出众的", "才华横溢的"
        };
        List<String> objects = (List<String>) CollectionUtils.arrayToList(adjectives);
        Set<String> set = new HashSet<>(objects);
        //将set转换为数组
        adjectives = set.toArray(new String[set.size()]);
        // 名词列表
        String[] nouns = {
                "狮子", "老虎", "熊猫", "兔子", "猫", "狗", "龙", "凤凰", "独角兽", "精灵",
                "海豚", "鲨鱼", "蝴蝶", "鹦鹉", "鸽子", "蜜蜂", "鹿", "马", "羊", "牛",
                "猪", "鸡", "鸭", "鹅", "熊", "狐狸", "狼", "豹", "鹰", "蛇",
                "鼠", "猴", "鱼", "鸟", "龟", "螃蟹", "章鱼", "海星", "海马", "海螺",
                "鲸鱼", "蝙蝠", "浣熊", "袋鼠", "考拉", "长颈鹿", "犀牛", "斑马", "河马", "企鹅",
                "海狮", "海象", "海鸥", "信天翁", "老鹰", "猫头鹰", "天鹅", "孔雀", "鹦鹉", "蜂鸟",
                // 添加蔬菜和水果
                "苹果", "香蕉", "橙子", "柠檬", "葡萄", "草莓", "樱桃", "蓝莓", "桃子", "梨",
                "西瓜", "甜瓜", "哈密瓜", "菠萝", "芒果", "猕猴桃", "木瓜", "椰子", "柚子", "橙汁",
                "胡萝卜", "土豆", "番茄", "黄瓜", "茄子", "菜花", "西兰花", "白菜", "菠菜", "芹菜",
                "辣椒", "南瓜", "豆角", "豌豆", "大蒜", "洋葱", "生姜", "大蒜瓣", "蘑菇", "甜椒"
                // ... 添加更多名词
        };

        Random random = new Random();

        // 随机选取形容词和名词
        String adjective = adjectives[random.nextInt(adjectives.length-1)];
        String noun = nouns[random.nextInt(nouns.length)];

        // 拼接形容词和名词
        return adjective + noun;
    }
}