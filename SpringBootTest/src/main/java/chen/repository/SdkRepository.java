package chen.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import chen.domain.TSdk;

@Repository
public interface SdkRepository extends CrudRepository<TSdk, Long> {

	List<TSdk> findByIp(String ip);

	Page<TSdk> findAll(Pageable pageable);

	List<TSdk> findByIpAndPort(String ip, String port);

	@Query("select max(sdkOrder) from TSdk")
	Integer findMaxOrder();

	@Query("select min(sdkOrder) from TSdk")
	Integer findMinOrder();

	List<TSdk> findBySdkOrder(Integer sdkOrder);

	@Query("from TSdk order by sdkOrder")
	List<TSdk> findAllOrderByOrder();

}
