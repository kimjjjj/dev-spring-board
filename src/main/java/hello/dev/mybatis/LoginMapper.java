package hello.dev.mybatis;

import hello.dev.domain.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface LoginMapper {
    Member login(@Param("userId") String userId, @Param("password") String password, @Param("provider") String provider);

    Member checkId(String userId);
}
