package com.dev.aes.util;

import org.springframework.stereotype.Service;

@Service
public class PaginationServiceImpl implements Pagination {


    @Override
    public Integer getstartlimit(Integer itemperpage, Integer pageno) {

        return (((pageno - 1) * itemperpage) + 1);
    }


    @Override
    public Integer getendlimit(Integer itemperpage, Integer pageno) {

        return (itemperpage * pageno);
    }


    @Override
    public Integer gettotalnoofpage(Integer totalamount, Integer itemperpage) {

        if (totalamount <= itemperpage) {

            return 1;

        } else {

            if (itemperpage == 1){

                return totalamount;
            }

            else if (totalamount % itemperpage == 0) {

                return (totalamount / itemperpage);

            } else {

                return ((totalamount / itemperpage) + (1));
            }
        }

    }

}// end class
