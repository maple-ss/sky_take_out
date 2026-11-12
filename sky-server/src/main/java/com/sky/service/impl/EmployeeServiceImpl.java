package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.PasswordConstant;
import com.sky.constant.StatusConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.exception.AccountAlreadyExistsException;
import com.sky.exception.AccountLockedException;
import com.sky.exception.AccountNotFoundException;
import com.sky.exception.EmployeeIdRequiredException;
import com.sky.exception.EmployeeStatusException;
import com.sky.exception.PasswordErrorException;
import com.sky.mapper.EmployeeMapper;
import com.sky.result.PageResult;
import com.sky.service.EmployeeService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
@Service
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @Override
    public Employee login(EmployeeLoginDTO employeeLoginDTO) {
        String username = employeeLoginDTO.getUsername();
        String password = employeeLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Employee employee = employeeMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对、账号被锁定）
        if (employee == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //对前端传过来的密码进行md5进行加密
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(employee.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        if (employee.getStatus() == StatusConstant.DISABLE) {
            //账号被锁定
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return employee;
    }

    /**
     * 新增员工
     *
     * @param employeeDTO
     */
    @Override
    public void save(EmployeeDTO employeeDTO) {

        //判断用户名是否存在
        if (employeeMapper.getByUsername(employeeDTO.getUsername()) != null) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }


        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        employee.setStatus(StatusConstant.ENABLE);
        employee.setPassword(DigestUtils.md5DigestAsHex(
                PasswordConstant.DEFAULT_PASSWORD.getBytes(StandardCharsets.UTF_8)));

        try {
            employeeMapper.insert(employee);
        } catch (DuplicateKeyException ex) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
    }

    /**
     * 员工分页查询
     *
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        PageHelper.startPage(employeePageQueryDTO.getPage(), employeePageQueryDTO.getPageSize());
        Page<Employee> page = employeeMapper.pageQuery(employeePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 启用、禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        if (!StatusConstant.ENABLE.equals(status) && !StatusConstant.DISABLE.equals(status)) {
            throw new EmployeeStatusException(MessageConstant.EMPLOYEE_STATUS_ERROR);
        }

        Employee employee = Employee.builder()
                .id(id)
                .status(status)
                .build();

        if (employeeMapper.updateStatus(employee) == 0) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
    }

    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    @Override
    public Employee getById(Long id) {
        if (id == null) {
            throw new EmployeeIdRequiredException(MessageConstant.EMPLOYEE_ID_REQUIRED);
        }

        Employee employee = employeeMapper.getById(id);
        if (employee == null) {
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }
        return employee;
    }

    /**
     * 编辑员工信息
     *
     * @param employeeDTO
     */
    @Override
    public void update(EmployeeDTO employeeDTO) {
        if (employeeDTO.getId() == null) {
            throw new EmployeeIdRequiredException(MessageConstant.EMPLOYEE_ID_REQUIRED);
        }

        Employee employeeWithUsername = employeeMapper.getByUsername(employeeDTO.getUsername());
        if (employeeWithUsername != null
                && !employeeDTO.getId().equals(employeeWithUsername.getId())) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }

        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);

        try {
            if (employeeMapper.update(employee) == 0) {
                throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
            }
        } catch (DuplicateKeyException ex) {
            throw new AccountAlreadyExistsException(MessageConstant.ACCOUNT_ALREADY_EXISTS);
        }
    }
}
