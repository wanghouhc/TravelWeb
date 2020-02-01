package utils;

import java.util.Arrays;

public class PageUtils {

    /**
     * 计算分页时limit条件的index值
     * @param pageNumber 当前是第几页
     * @param pageSize 每页显示多少条
     * @return 分页时limit的index值
     */
    public static int calcSqlLimitIndex(int pageNumber, int pageSize){
        return (pageNumber - 1) * pageSize;
    }

    /**
     * 计算分了多少页
     * @param totalCount 要分页的总数量
     * @param pageSize 每页显示多少条
     * @return 分了多少页
     */
    public static int calcPageCount(int totalCount, int pageSize){
        return (int) Math.ceil(totalCount * 1.0 / pageSize);
    }

    /**
     * 10页-分页条：前5后4动态分页，计算起始页
     *
     * @param pageNumber 当前页码
     * @param pageCount  总共有多少页
     * @return int[0]：开始页码； int[1]：结束页面
     */
    public static int[] pagination(int pageNumber, int pageCount) {
        //初始化开始页为1
        int start = 1;
        //初始化结束页为总页数
        int end = pageCount;

        //如果总页数超过10页，需要计算开始页和结束页
        if (pageCount > 10) {
            //计算开始页
            start = (pageNumber <= 5) ? 1 : (pageNumber - 5);
            //计算结束页：开始页面之后，再显示9个页码，共10个页码
            end = start + 9;
            //处理结束页越界的情况
            if (end > pageCount) {
                end = pageCount;
                start = end - 9;
            }
        }
        return new int[]{start, end};
    }

    public static void main(String[] args) {
        int[] pagination = pagination(7, 11);
        System.out.println(Arrays.toString(pagination));
    }
}
