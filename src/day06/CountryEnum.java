package day06;

/**
 * @author chenxiaonuo
 * @date 2019-08-14 14:51
 */
public enum CountryEnum {

    ONE(1,"齐"),
    TWO(2,"楚"),
    THREE(3,"燕"),
    FOUR(4,"赵"),
    FIVE(5,"魏"),
    SIX(6,"韩");

    private Integer retcode;
    private String retMessage;

    CountryEnum(Integer retcode, String retMessage) {
        this.retcode = retcode;
        this.retMessage = retMessage;
    }

    public Integer getRetcode() {
        return retcode;
    }

    public String getRetMessage() {
        return retMessage;
    }

    public static CountryEnum forEach_CountryEnum(int index){
        CountryEnum[] values = CountryEnum.values();
        for (CountryEnum value : values) {
            if (index == value.getRetcode()){
                return value;
            }
        }
        return null;
    }
}
