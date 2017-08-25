package chen.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import chen.domain.TChanel;

@Repository
public interface ChanelRepository extends CrudRepository<TChanel, Long> {

	List<TChanel> findBySdk_IpOrderByChanelOrderAsc(String ip);

	Page<TChanel> findAll(Pageable pageable);

	@Query("select max(chanelOrder) from TChanel where sdk.id = :id")
	Integer findMaxOrder(@Param("id") Long id);

	@Query("select min(chanelOrder) from TChanel where sdk.id = :id")
	Integer findMinOrder(@Param("id") Long id);

	List<TChanel> findByChanelOrder(Integer chanelOrder);

	Page<TChanel> findBySdk_IdOrderByChanelOrderAsc(Long id, Pageable pageable);

	List<TChanel> findByChanelOrderAndSdk_Id(Integer chanelOrder, Long sdkid);

	List<TChanel> findBySdk_IdOrderByChanelOrderAsc(Long id);
}
