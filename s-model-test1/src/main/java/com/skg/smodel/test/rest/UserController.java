package com.skg.smodel.test.rest;

import com.github.pagehelper.PageHelper;
import com.skg.smodel.test.biz.UserBiz;
import com.skg.smodel.test.entity.BaseUser;
import com.skg.smodel.core.msg.TableResultResponse;
import com.skg.smodel.core.rest.BaseController;
import com.skg.smodel.core.msg.ListRestResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;


@Controller
@RequestMapping("/")
public class UserController extends BaseController<UserBiz,BaseUser> {

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    @ResponseBody
    public TableResultResponse<BaseUser> page(@RequestParam(defaultValue = "10") int limit, @RequestParam(defaultValue = "1")int offset, String name){
        Example example = new Example(BaseUser.class);
        if(StringUtils.isNotBlank(name)) {
            example.createCriteria().andLike("name", "%" + name + "%");
            example.createCriteria().andLike("username", "%" + name + "%");
        }
        int count = baseBiz.selectCountByExample(example);
        PageHelper.startPage(offset, limit);
        return new TableResultResponse<BaseUser>(count,baseBiz.selectByExample(example));
    }


}
