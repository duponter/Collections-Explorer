import * as React from 'react';
import { Avatar, Table } from 'antd';

class BoardGameOverview extends React.Component {

    render() {
        const dataSource = [
            {
                key: '1',
                img: 'https://cf.geekdo-images.com/itemrep/img/xfyi8wC0xQFtW8Ku2uuxWDNxGTI=/fit-in/246x300/pic145204.jpg',
                title: 'Ur',
                year: 2006,
            },
            {
                key: '2',
                img: 'https://cf.geekdo-images.com/itemrep/img/RrR24v4sWcGbp7bCzzn6nlHjzg4=/fit-in/246x300/pic4892981.jpg',
                title: 'Nemo\'s War (Second Edition)',
                year: 2017,
            },
        ];

        const columns = [
            {
                title: '',
                dataIndex: 'img',
                key: 'img',
                render: (text: string, record: any, index: number) => <Avatar size={64} icon={<img alt={record.title} src={text} />} />
            },
            {
                title: 'Title',
                dataIndex: 'title',
                key: 'title',
            },
            {
                title: 'Year',
                dataIndex: 'year',
                key: 'year',
            },
        ];

        return (
            <>

                <Table dataSource={dataSource} columns={columns} />
            </>);
    }
}

export default BoardGameOverview;
