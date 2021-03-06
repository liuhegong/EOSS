
/**
* Author:Collonn, Email:collonn@126.com, QQ:195358385
*/
package com.jelly.eoss.db.mapper.basic.iface;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.jelly.eoss.db.entity.AdminMenu;

@Mapper
@Repository
public interface AdminMenuMapper {



    public Integer selectCount(AdminMenu adminMenu);
    public List<AdminMenu> selectPage(AdminMenu adminMenu);

    public List<AdminMenu> select(AdminMenu adminMenu);
    public AdminMenu selectOne(AdminMenu adminMenu);
    public List<AdminMenu> selectAll();
    public AdminMenu selectByPk(Integer id);

    public int insert(AdminMenu adminMenu);
    public int update(AdminMenu adminMenu);
    public int updateWithNull(AdminMenu adminMenu);
    public int deleteByPk(Integer id);
    public int delete(AdminMenu adminMenu);
}