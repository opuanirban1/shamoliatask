package com.dev.aes.util;

public interface Pagination {

    Integer getstartlimit (Integer itemperpage, Integer pageno);
    Integer getendlimit (Integer itemperpage, Integer pageno);
    Integer gettotalnoofpage (Integer totalamount, Integer itemperpage);
}
