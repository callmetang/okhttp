package  com.demo.http;


/**
 * @author tang
 * @date 2019/1/29
 */
public class ResultBean<T> {

    private int error_code;
    private String error_message;
    private T data;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_message() {
        return error_message;
    }

    public void setError_message(String error_message) {
        this.error_message = error_message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "error_code=" + error_code +
                ", error_message='" + error_message + '\'' +
                ", data=" + data +
                '}';
    }
}
