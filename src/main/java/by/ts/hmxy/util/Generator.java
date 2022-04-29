package by.ts.hmxy.util;

import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;

/**通过runGen.lanch启动，用于生成一些文件，比如日志*/
public class Generator {
	public static void main(String[] args) throws NoHeadException, GitAPIException, IOException {
		Git.open(new File("D:\\WorkSpaces\\Eclipse\\HongMengXianYuan-1.17")).log().all().call().forEach(c->{
			System.out.println(c.getFullMessage());
		});
		Git.shutdown();
	}
}
